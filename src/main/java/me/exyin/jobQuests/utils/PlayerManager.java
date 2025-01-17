package me.exyin.jobQuests.utils;

import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.model.Objective;
import me.exyin.jobQuests.model.Quest;
import me.exyin.jobQuests.model.player.JQPlayer;
import me.exyin.jobQuests.model.player.PlayerJob;
import me.exyin.jobQuests.model.player.PlayerObjective;
import me.exyin.jobQuests.model.player.PlayerQuest;
import me.exyin.jobQuests.model.rewards.RewardFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerManager {
    private final JobQuests jobQuests;
    @Getter
    private final List<JQPlayer> jqPlayers;

    public PlayerManager(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        this.jqPlayers = new ArrayList<>();
    }

    public boolean isPlayerLoaded(UUID uuid) {
        return !jqPlayers.stream().filter(jqPlayer -> jqPlayer.getUuid() == uuid).toList().isEmpty();
    }

    public void unloadPlayer(UUID uuid) {
        jqPlayers.remove(jqPlayers.stream().filter(jqPlayer -> jqPlayer.getUuid() == uuid).toList().getFirst());
    }

    public JQPlayer getJQPlayer(UUID uuid) {
        return jqPlayers.stream().filter(jqPlayer -> jqPlayer.getUuid() == uuid).toList().getFirst();
    }

    public PlayerJob getPlayerJob(UUID uuid, String jobId) {
        return getJQPlayer(uuid).getPlayerJobs().stream().filter(playerJob -> playerJob.getJobId().equals(jobId)).toList().getFirst();
    }

    public PlayerQuest getPlayerQuest(UUID uuid, String jobId, int questId) {
        return getPlayerJob(uuid, jobId).getPlayerQuests().stream().filter(playerQuest -> playerQuest.getQuestId() == questId).toList().getFirst();
    }

    public PlayerObjective getPlayerObjective(UUID uuid, String jobId, int questId, int objectiveId) {
        return getPlayerQuest(uuid, jobId, questId).getPlayerObjectives().stream().filter(playerObjective -> playerObjective.getObjectiveId() == objectiveId).toList().getFirst();
    }

    public void incrementProgression(UUID uuid, String jobId, int questId, int objectiveId) {
        PlayerObjective playerObjective = getPlayerObjective(uuid, jobId, questId, objectiveId);
        playerObjective.setProgression(playerObjective.getProgression() + 1);
    }

    public boolean checkQuestCompletion(UUID uuid, String jobId, int questId) {
        for (Objective objective : jobQuests.getJobManager().getQuest(jobId, questId).getObjectives()) {
            if (getPlayerObjective(uuid, jobId, questId, objective.getId()).getProgression() < objective.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public void giveRewards(UUID uuid, String jobId, int questId) {
        jobQuests.getJobManager().getQuest(jobId, questId).getRewards().forEach(reward -> {
            RewardFactory rewardFactory = new RewardFactory(jobQuests);
            rewardFactory.getStrategy(reward.getType()).giveReward(uuid, jobId, reward.getQuantity());
        });
    }

    public long calculateJobLevel(double jobXp, int level) {
        double nextLevelRequiredXp = jobQuests.getConfigManager().getJobXpLevelUpRequirementBase() * Math.pow(jobQuests.getConfigManager().getJobXpLevelUpRequirementMultiplier(), level - 1);
        if (jobXp < nextLevelRequiredXp) {
            return level;
        }
        return calculateJobLevel(jobXp - nextLevelRequiredXp, level + 1);
    }

    public void refreshPlayerQuest(UUID uuid, String jobId, int questId) {
        PlayerQuest playerQuest = getPlayerQuest(uuid, jobId, questId);
        playerQuest.setCompletedDate(null);
        playerQuest.getPlayerObjectives().forEach(playerObjective -> playerObjective.setProgression(0));
        Quest quest = jobQuests.getJobManager().getQuest(jobId, questId);
        jobQuests.getMessageManager().sendMessage(jobQuests.getServer().getPlayer(uuid), MessageFormat.format(jobQuests.getMessageConfig().getQuestRefreshed(), quest.getTitle()));
    }

    private void createJQPlayer(UUID uuid) {
        List<PlayerJob> playerJobs = new ArrayList<>();
        jobQuests.getJobManager().getJobs().forEach(job -> {
            PlayerJob playerJob = createPlayerJob(job);
            playerJobs.add(playerJob);
        });
        jqPlayers.add(new JQPlayer(uuid, playerJobs));
    }

    private PlayerJob createPlayerJob(Job job) {
        List<PlayerQuest> playerQuests = new ArrayList<>();
        job.getQuests().forEach(quest -> {
            PlayerQuest playerQuest = createPlayerQuest(quest);
            playerQuests.add(playerQuest);
        });
        return new PlayerJob(job.getId(), 0, playerQuests);
    }

    private PlayerQuest createPlayerQuest(Quest quest) {
        List<PlayerObjective> playerObjectives = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = createPlayerObjective(objective);
            playerObjectives.add(playerObjective);
        });
        return new PlayerQuest(quest.getId(), null, playerObjectives);
    }

    private PlayerObjective createPlayerObjective(Objective objective) {
        return new PlayerObjective(objective.getId(), 0);
    }

    public void loadPlayer(UUID uuid) {
        String playerFilePath = jobQuests.getDataFolder().getPath() + File.separator + "data" + File.separator + uuid.toString() + ".yml";
        File playerFile = new File(playerFilePath);
        if (!playerFile.exists()) {
            playerFile.getParentFile().mkdirs();
            createJQPlayer(uuid);
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        List<PlayerJob> playerJobs = loadPlayerJobs(yaml);
        jqPlayers.add(new JQPlayer(uuid, playerJobs));
    }

    private List<PlayerJob> loadPlayerJobs(YamlConfiguration yaml) {
        ConfigurationSection playerJobsSection = yaml.getConfigurationSection("jobs");
        List<PlayerJob> playerJobs = new ArrayList<>();
        if (playerJobsSection == null) {
            return playerJobs;
        }
        for (String jobKey : playerJobsSection.getKeys(false)) {
            PlayerJob playerJob = loadPlayerJob(playerJobsSection, jobKey);
            if (playerJob == null) {
                continue;
            }
            playerJobs.add(playerJob);
        }
        return playerJobs;
    }

    private PlayerJob loadPlayerJob(ConfigurationSection playerJobsSection, String jobKey) {
        ConfigurationSection playerJobSection = playerJobsSection.getConfigurationSection(jobKey);
        if (playerJobSection == null) {
            return null;
        }
        double xp = playerJobSection.getDouble("xp");
        List<PlayerQuest> playerQuests = loadPlayerQuests(playerJobSection);
        return new PlayerJob(jobKey, xp, playerQuests);
    }

    private List<PlayerQuest> loadPlayerQuests(ConfigurationSection playerJobSection) {
        ConfigurationSection playerQuestsSection = playerJobSection.getConfigurationSection("quests");
        List<PlayerQuest> playerQuests = new ArrayList<>();
        if (playerQuestsSection == null) {
            return playerQuests;
        }
        for (String questKey : playerQuestsSection.getKeys(false)) {
            PlayerQuest playerQuest = loadPlayerQuest(playerQuestsSection, questKey);
            if (playerQuest == null) {
                continue;
            }
            playerQuests.add(playerQuest);
        }
        return playerQuests;
    }

    private PlayerQuest loadPlayerQuest(ConfigurationSection playerQuestsSection, String questKey) {
        ConfigurationSection playerQuestSection = playerQuestsSection.getConfigurationSection(questKey);
        if (playerQuestSection == null) {
            return null;
        }
        try {
            int questId = Integer.parseInt(questKey);
            LocalDateTime completedDate = null;
            String date = playerQuestSection.getString("completedDate");
            if (date != null) {
                completedDate = LocalDateTime.from(jobQuests.getTimeManager().getFormatter().parse(date));
            }
            List<PlayerObjective> playerObjectives = loadPlayerObjectives(playerQuestSection);
            return new PlayerQuest(questId, completedDate, playerObjectives);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<PlayerObjective> loadPlayerObjectives(ConfigurationSection playerQuestSection) {
        ConfigurationSection playerObjectivesSection = playerQuestSection.getConfigurationSection("objectives");
        List<PlayerObjective> playerObjectives = new ArrayList<>();
        if (playerObjectivesSection == null) {
            return playerObjectives;
        }
        for (String objectiveKey : playerObjectivesSection.getKeys(false)) {
            PlayerObjective playerObjective = loadPlayerObjective(playerObjectivesSection, objectiveKey);
            if (playerObjective == null) {
                continue;
            }
            playerObjectives.add(playerObjective);
        }
        return playerObjectives;
    }

    private PlayerObjective loadPlayerObjective(ConfigurationSection playerObjectivesSection, String objectiveKey) {
        ConfigurationSection playerObjectiveSection = playerObjectivesSection.getConfigurationSection(objectiveKey);
        if (playerObjectiveSection == null) {
            return null;
        }
        try {
            int objectiveId = Integer.parseInt(objectiveKey);
            int progression = playerObjectiveSection.getInt("progression");
            return new PlayerObjective(objectiveId, progression);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void updatePlayer(UUID uuid) {
        JQPlayer jqPlayer = getJQPlayer(uuid);
        jobQuests.getJobManager().getJobs().forEach(job -> {
            if (jqPlayer.getPlayerJobs().stream().filter(playerJob -> playerJob.getJobId().equals(job.getId())).toList().isEmpty()) {
                jqPlayer.getPlayerJobs().add(createPlayerJob(job));
                return;
            }
            PlayerJob playerJob = jqPlayer.getPlayerJobs().stream().filter(playerJob1 -> playerJob1.getJobId().equals(job.getId())).toList().getFirst();
            List<PlayerQuest> questsToRemove = new ArrayList<>();
            playerJob.getPlayerQuests().forEach(playerQuest -> {
                if (job.getQuests().stream().filter(quest -> quest.getId() == playerQuest.getQuestId()).toList().isEmpty()) {
                    questsToRemove.add(playerQuest);
                } else {
                    Quest jobQuest = job.getQuests().stream().filter(quest -> quest.getId() == playerQuest.getQuestId()).toList().getFirst();
                    List<PlayerObjective> objectivesToRemove = new ArrayList<>();
                    playerQuest.getPlayerObjectives().forEach(playerObjective -> {
                        if (jobQuest.getObjectives().stream().filter(objective -> objective.getId() == playerObjective.getObjectiveId()).toList().isEmpty()) {
                            objectivesToRemove.add(playerObjective);
                        }
                    });
                    playerQuest.getPlayerObjectives().removeAll(objectivesToRemove);
                    if (playerQuest.getPlayerObjectives().size() < jobQuest.getObjectives().size()) {
                        jobQuest.getObjectives().forEach(objective -> {
                            if (playerQuest.getPlayerObjectives().stream().filter(playerObjective -> playerObjective.getObjectiveId() == objective.getId()).toList().isEmpty()) {
                                playerQuest.getPlayerObjectives().add(createPlayerObjective(objective));
                            }
                        });
                    }
                }
            });
            playerJob.getPlayerQuests().removeAll(questsToRemove);
            if (playerJob.getPlayerQuests().size() < job.getQuests().size()) {
                job.getQuests().forEach(quest -> {
                    if (playerJob.getPlayerQuests().stream().filter(playerQuest -> playerQuest.getQuestId() == quest.getId()).toList().isEmpty()) {
                        playerJob.getPlayerQuests().add(createPlayerQuest(quest));
                    }
                });
            }
        });
    }

    public void purgePlayerJobs(UUID uuid) {
        if (!isPlayerLoaded(uuid)) {
            loadPlayer(uuid);
        }
        JQPlayer jqPlayer = getJQPlayer(uuid);
        List<PlayerJob> jobsToRemove = new ArrayList<>();
        jqPlayer.getPlayerJobs().forEach(playerJob -> {
            if (jobQuests.getJobManager().getJobs().stream().filter(job -> job.getId().equals(playerJob.getJobId())).toList().isEmpty()) {
                jobsToRemove.add(playerJob);
            }
        });
        jqPlayer.getPlayerJobs().removeAll(jobsToRemove);
        if (!Objects.requireNonNull(Bukkit.getPlayer(uuid)).isOnline()) {
            unloadPlayer(uuid);
        }
    }

    public void savePlayer(UUID uuid) {
        JQPlayer jqPlayer = getJQPlayer(uuid);
        String playerFilePath = jobQuests.getDataFolder().getPath() + File.separator + "data" + File.separator + uuid.toString() + ".yml";
        File playerFile = new File(playerFilePath);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        yaml.set("uuid", uuid.toString());
        ConfigurationSection playerJobsSection = yaml.createSection("jobs");
        jqPlayer.getPlayerJobs().forEach(playerJob -> {
            ConfigurationSection playerJobSection = playerJobsSection.createSection(playerJob.getJobId());
            playerJobSection.set("xp", playerJob.getXp());
            ConfigurationSection playerQuestsSection = playerJobSection.createSection("quests");
            playerJob.getPlayerQuests().forEach(playerQuest -> {
                ConfigurationSection playerQuestSection = playerQuestsSection.createSection(String.valueOf(playerQuest.getQuestId()));
                playerQuestSection.set("completedDate", playerQuest.getCompletedDate() == null ? null : playerQuest.getCompletedDate().format(jobQuests.getTimeManager().getFormatter()));
                ConfigurationSection playerObjectivesSection = playerQuestSection.createSection("objectives");
                playerQuest.getPlayerObjectives().forEach(playerObjective -> {
                    ConfigurationSection playerObjectiveSection = playerObjectivesSection.createSection(String.valueOf(playerObjective.getObjectiveId()));
                    playerObjectiveSection.set("progression", playerObjective.getProgression());
                });
            });
        });
        try {
            yaml.save(playerFile);
        } catch (IOException e) {
            jobQuests.getLogger().severe(MessageFormat.format("Error while trying to save file {0}.", playerFilePath));
        }
    }
}
