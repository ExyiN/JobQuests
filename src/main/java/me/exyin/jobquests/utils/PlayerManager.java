package me.exyin.jobquests.utils;

import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.Objective;
import me.exyin.jobquests.model.Quest;
import me.exyin.jobquests.model.player.JQPlayer;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerObjective;
import me.exyin.jobquests.model.player.PlayerQuest;
import me.exyin.jobquests.model.rewards.RewardFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public double getNextLevelRequiredXp(long level) {
        return jobQuests.getConfigManager().getJobXpLevelUpRequirementBase() + Math.pow(level - 1.0, 2.0) * 5;
    }

    public void setJobLevel(UUID uuid, String jobId, long level) {
        if (!isPlayerLoaded(uuid)) {
            loadPlayer(uuid);
        }
        PlayerJob playerJob = getPlayerJob(uuid, jobId);
        double xpPercent = playerJob.getXp() / getNextLevelRequiredXp(playerJob.getLevel());
        playerJob.setLevel(level);
        if (playerJob.getXp() >= getNextLevelRequiredXp(level)) {
            playerJob.setXp(Math.round(getNextLevelRequiredXp(level) * xpPercent));
        }
        if (!jobQuests.getServer().getOfflinePlayer(uuid).isOnline()) {
            savePlayer(uuid);
            unloadPlayer(uuid);
        }
    }

    public long changeJobLevel(UUID uuid, String jobId) {
        PlayerJob playerJob = getPlayerJob(uuid, jobId);
        long newLevel = playerJob.getLevel();
        double jobXp = playerJob.getXp();
        boolean loopEnd = false;
        do {
            double xpRequirement = getNextLevelRequiredXp(newLevel);
            if (jobXp >= xpRequirement) {
                jobXp -= xpRequirement;
                newLevel++;
            } else {
                loopEnd = true;
            }
        } while (!loopEnd);
        playerJob.setLevel(newLevel);
        playerJob.setXp(jobXp);
        return newLevel;
    }

    public void resetPlayerJob(UUID uuid, String jobId) {
        if (!isPlayerLoaded(uuid)) {
            loadPlayer(uuid);
        }
        PlayerJob playerJob = getPlayerJob(uuid, jobId);
        playerJob.setLevel(1);
        playerJob.setXp(0);
        playerJob.getPlayerQuests().forEach(playerQuest -> resetPlayerQuest(uuid, jobId, playerQuest.getQuestId()));
    }

    public void resetPlayerQuest(UUID uuid, String jobId, int questId) {
        if (!isPlayerLoaded(uuid)) {
            loadPlayer(uuid);
        }
        PlayerQuest playerQuest = getPlayerQuest(uuid, jobId, questId);
        playerQuest.setCompletedDate(null);
        playerQuest.getPlayerObjectives().forEach(playerObjective -> playerObjective.setProgression(0));
        if (!jobQuests.getServer().getOfflinePlayer(uuid).isOnline()) {
            savePlayer(uuid);
            unloadPlayer(uuid);
        }
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
        return new PlayerJob(job.getId(), 1, 0, playerQuests);
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

    public void loadAllOfflinePlayers() {
        File playerDataFolder = new File(jobQuests.getDataFolder(), "data");
        File[] playerDataFiles = playerDataFolder.listFiles();
        if (!playerDataFolder.exists() || playerDataFiles == null) {
            return;
        }
        for (File playerDataFile : playerDataFiles) {
            int dotIndex = playerDataFile.getName().lastIndexOf('.');
            UUID playerUuid = UUID.fromString(playerDataFile.getName().substring(0, dotIndex));
            if (isPlayerLoaded(playerUuid)) {
                continue;
            }
            loadPlayer(playerUuid);
        }
    }

    public void unloadAllOfflinePlayers() {
        List<JQPlayer> jqPlayerIterator = new ArrayList<>(jqPlayers);
        jqPlayerIterator.forEach(jqPlayer -> {
            if (jobQuests.getServer().getOfflinePlayer(jqPlayer.getUuid()).isOnline()) {
                return;
            }
            unloadPlayer(jqPlayer.getUuid());
        });
    }

    public void loadPlayer(UUID uuid) {
        String playerFilePath = jobQuests.getDataFolder().getPath() + File.separator + "data" + File.separator + uuid.toString() + ".yml";
        File playerFile = new File(playerFilePath);
        if (!playerFile.getParentFile().exists()) {
            boolean isFolderCreated = playerFile.getParentFile().mkdirs();
            if (isFolderCreated) {
                jobQuests.getLogger().info("Data folder not found. Creating data folder.");
            }
        }
        if (!playerFile.exists()) {
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
        long level = playerJobSection.getLong("level");
        double xp = playerJobSection.getDouble("xp");
        List<PlayerQuest> playerQuests = loadPlayerQuests(playerJobSection);
        return new PlayerJob(jobKey, level, xp, playerQuests);
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
                completedDate = LocalDateTime.from(jobQuests.getTimeUtil().getFormatter().parse(date));
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
            PlayerJob playerJob = getPlayerJob(uuid, job.getId());
            List<PlayerQuest> questsToRemove = new ArrayList<>();
            playerJob.getPlayerQuests().forEach(playerQuest -> {
                if (!jobQuests.getJobManager().existsQuest(job.getId(), playerQuest.getQuestId())) {
                    questsToRemove.add(playerQuest);
                } else {
                    Quest quest = jobQuests.getJobManager().getQuest(job.getId(), playerQuest.getQuestId());
                    List<PlayerObjective> objectivesToRemove = new ArrayList<>();
                    playerQuest.getPlayerObjectives().forEach(playerObjective -> {
                        if (!jobQuests.getJobManager().existsObjective(job.getId(), quest.getId(), playerObjective.getObjectiveId())) {
                            objectivesToRemove.add(playerObjective);
                        }
                    });
                    playerQuest.getPlayerObjectives().removeAll(objectivesToRemove);
                    if (playerQuest.getPlayerObjectives().size() < quest.getObjectives().size()) {
                        quest.getObjectives().forEach(objective -> {
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

    public List<String> purgePlayerJobs(UUID uuid) {
        if (!isPlayerLoaded(uuid)) {
            loadPlayer(uuid);
        }
        JQPlayer jqPlayer = getJQPlayer(uuid);
        List<PlayerJob> jobsToRemove = new ArrayList<>();
        jqPlayer.getPlayerJobs().forEach(playerJob -> {
            if (!jobQuests.getJobManager().existsJob(playerJob.getJobId())) {
                jobsToRemove.add(playerJob);
            }
        });
        jqPlayer.getPlayerJobs().removeAll(jobsToRemove);
        if (!jobQuests.getServer().getOfflinePlayer(uuid).isOnline()) {
            savePlayer(uuid);
            unloadPlayer(uuid);
        }
        return jobsToRemove.stream().map(PlayerJob::getJobId).toList();
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
            playerJobSection.set("level", playerJob.getLevel());
            playerJobSection.set("xp", playerJob.getXp());
            ConfigurationSection playerQuestsSection = playerJobSection.createSection("quests");
            playerJob.getPlayerQuests().forEach(playerQuest -> {
                ConfigurationSection playerQuestSection = playerQuestsSection.createSection(String.valueOf(playerQuest.getQuestId()));
                playerQuestSection.set("completedDate", playerQuest.getCompletedDate() == null ? null : playerQuest.getCompletedDate().format(jobQuests.getTimeUtil().getFormatter()));
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
