package me.exyin.jobQuests.utils;

import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.*;
import me.exyin.jobQuests.model.player.JQPlayer;
import me.exyin.jobQuests.model.player.PlayerJob;
import me.exyin.jobQuests.model.player.PlayerObjective;
import me.exyin.jobQuests.model.player.PlayerQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private final JobQuests jobQuests;
    @Getter
    private final Set<JQPlayer> jqPlayers;

    public PlayerManager(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        this.jqPlayers = new HashSet<>();
    }

    public boolean isPlayerLoaded(UUID uuid) {
        return !jqPlayers.stream().filter(jqPlayer -> jqPlayer.getUuid() == uuid).toList().isEmpty();
    }

    public void unloadPlayer(UUID uuid) {
        jqPlayers.remove(jqPlayers.stream().filter(jqPlayer -> jqPlayer.getUuid() == uuid).toList().getFirst());
    }

    private void createJQPlayer(UUID uuid) {
        Set<PlayerJob> playerJobs = new HashSet<>();
        jobQuests.getJobs().forEach(job -> {
            PlayerJob playerJob = createPlayerJob(job);
            playerJobs.add(playerJob);
        });
        jqPlayers.add(new JQPlayer(uuid, playerJobs));
    }

    private PlayerJob createPlayerJob(Job job) {
        Set<PlayerQuest> playerQuests = new HashSet<>();
        job.getQuests().forEach(quest -> {
            PlayerQuest playerQuest = createPlayerQuest(quest);
            playerQuests.add(playerQuest);
        });
        return new PlayerJob(job.getId(), 0, playerQuests);
    }

    private PlayerQuest createPlayerQuest(Quest quest) {
        Set<PlayerObjective> playerObjectives = new HashSet<>();
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
        Set<PlayerJob> playerJobs = loadPlayerJobs(yaml);
        jqPlayers.add(new JQPlayer(uuid, playerJobs));
    }

    private Set<PlayerJob> loadPlayerJobs(YamlConfiguration yaml) {
        ConfigurationSection playerJobsSection = yaml.getConfigurationSection("jobs");
        Set<PlayerJob> playerJobs = new HashSet<>();
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
        long xp = playerJobSection.getLong("xp");
        Set<PlayerQuest> playerQuests = loadPlayerQuests(playerJobSection);
        return new PlayerJob(jobKey, xp, playerQuests);
    }

    private Set<PlayerQuest> loadPlayerQuests(ConfigurationSection playerJobSection) {
        ConfigurationSection playerQuestsSection = playerJobSection.getConfigurationSection("quests");
        Set<PlayerQuest> playerQuests = new HashSet<>();
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
            Date completedDate = null;
            String date = playerQuestSection.getString("completedDate");
            if (date != null) {
                completedDate = SimpleDateFormat.getDateTimeInstance().parse(date);
            }
            Set<PlayerObjective> playerObjectives = loadPlayerObjectives(playerQuestSection);
            return new PlayerQuest(questId, completedDate, playerObjectives);
        } catch (NumberFormatException | ParseException e) {
            return null;
        }
    }

    private Set<PlayerObjective> loadPlayerObjectives(ConfigurationSection playerQuestSection) {
        ConfigurationSection playerObjectivesSection = playerQuestSection.getConfigurationSection("objectives");
        Set<PlayerObjective> playerObjectives = new HashSet<>();
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

    public void savePlayer(UUID uuid) {
        JQPlayer jqPlayer = jqPlayers.stream().filter(player -> player.getUuid() == uuid).toList().getFirst();
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
                playerQuestSection.set("completedDate", playerQuest.getCompletedDate() == null ? null : playerQuest.getCompletedDate().toString());
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
