package me.exyin.jobquests.utils;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.Objective;
import me.exyin.jobquests.model.Quest;
import me.exyin.jobquests.model.Reward;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.enums.RewardType;
import me.exyin.jobquests.model.objectives.ObjectiveFactory;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobLoader {
    private final JobQuests jobQuests;

    public JobLoader(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    public void saveDefaultJobs() {
        jobQuests.saveResource("jobs" + File.separator + "hunter.yml", false);
        jobQuests.saveResource("jobs" + File.separator + "lumberjack.yml", false);
    }

    public List<Job> loadAllJobs() {
        File jobDir = new File(jobQuests.getDataFolder(), "jobs");
        if (!jobDir.exists() || jobDir.listFiles() == null) {
            saveDefaultJobs();
        }
        List<Job> jobs = new ArrayList<>();
        File[] jobsFiles = jobDir.listFiles();
        if (jobsFiles == null) {
            return jobs;
        }
        YamlConfiguration yaml = new YamlConfiguration();
        for (File jobFile : jobsFiles) {
            try {
                yaml.load(jobFile);
                jobs.add(loadJob(yaml, jobFile.getPath()));
            } catch (IOException | InvalidConfigurationException e) {
                jobQuests.getLogger().severe(MessageFormat.format("Cannot read configuration for job: {0}", jobFile.getName()));
            }
        }
        return jobs;
    }

    public Job loadJob(YamlConfiguration jobYaml, String filePath) {
        String id = jobYaml.getString("id");
        String name = jobYaml.getString("name");
        Material material = Material.STONE;
        try {
            material = Material.valueOf(jobYaml.getString("material"));
        } catch (IllegalArgumentException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid material {1}.", filePath, jobYaml.getString("material")));
        }
        List<String> description = jobYaml.getStringList("description");
        List<Quest> quests = loadQuestsFromJob(jobYaml, filePath);
        return new Job(id, name, material, description, quests);
    }

    public List<Quest> loadQuestsFromJob(YamlConfiguration jobYaml, String filePath) {
        ConfigurationSection questsSection = jobYaml.getConfigurationSection("quests");
        List<Quest> quests = new ArrayList<>();
        if (questsSection == null) {
            return quests;
        }
        for (String questKey : questsSection.getKeys(false)) {
            Quest newQuest = loadQuest(questsSection, questKey, filePath);
            if (newQuest == null) {
                continue;
            }
            quests.add(newQuest);
        }
        return quests;
    }

    public Quest loadQuest(ConfigurationSection questsSection, String questKey, String filePath) {
        ConfigurationSection questSection = questsSection.getConfigurationSection(questKey);
        if (questSection == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(questKey);
            String title = questSection.getString("title");
            int requiredLevel = questSection.getInt("requiredLevel");
            String refreshTime = questSection.getString("refreshTime");
            List<Objective> objectives = loadObjectivesFromQuest(questSection, filePath);
            List<Reward> rewards = loadRewardsFromQuest(questSection, filePath);
            return new Quest(id, title, requiredLevel, refreshTime, objectives, rewards);
        } catch (NumberFormatException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid quest key {1}. It should be a number.", filePath, questKey));
            return null;
        }
    }

    private List<Objective> loadObjectivesFromQuest(ConfigurationSection questSection, String filePath) {
        ConfigurationSection objectivesSection = questSection.getConfigurationSection("objectives");
        List<Objective> objectives = new ArrayList<>();
        if (objectivesSection == null) {
            return objectives;
        }
        for (String objectiveKey : objectivesSection.getKeys(false)) {
            Objective newObjective = loadObjective(objectivesSection, objectiveKey, filePath);
            if (newObjective == null) {
                continue;
            }
            objectives.add(newObjective);
        }
        return objectives;
    }

    private Objective loadObjective(ConfigurationSection objectivesSection, String objectiveKey, String filePath) {
        ConfigurationSection objectiveSection = objectivesSection.getConfigurationSection(objectiveKey);
        if (objectiveSection == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(objectiveKey);
            ObjectiveEventType objectiveEventType = ObjectiveEventType.valueOf(objectiveSection.getString("eventType"));
            String type = objectiveSection.getString("type");
            ObjectiveFactory objectiveFactory = new ObjectiveFactory(jobQuests);
            ObjectiveType objectiveType = objectiveFactory.getStrategy(objectiveEventType);
            objectiveType.setType(type);
            int quantity = objectiveSection.getInt("quantity");
            return new Objective(id, objectiveEventType, objectiveType, quantity);
        } catch (NumberFormatException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid objective key {1} format. It should be a number.", filePath, objectiveKey));
        } catch (IllegalArgumentException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid objective event type {1}. Possible values: {2}", filePath, objectiveSection.getString("eventType"), Arrays.asList(ObjectiveEventType.values())));
        }
        return null;
    }

    private List<Reward> loadRewardsFromQuest(ConfigurationSection questSection, String filePath) {
        ConfigurationSection rewardsSection = questSection.getConfigurationSection("rewards");
        List<Reward> rewards = new ArrayList<>();
        if (rewardsSection == null) {
            return rewards;
        }
        for (String rewardKey : rewardsSection.getKeys(false)) {
            Reward newReward = loadReward(rewardsSection, rewardKey, filePath);
            if (newReward == null) {
                continue;
            }
            rewards.add(newReward);
        }
        return rewards;
    }

    private Reward loadReward(ConfigurationSection rewardsSection, String rewardKey, String filePath) {
        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardKey);
        if (rewardSection == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(rewardKey);
            RewardType type = RewardType.valueOf(rewardSection.getString("type"));
            double quantity = rewardSection.getDouble("quantity");
            return new Reward(id, type, quantity);
        } catch (NumberFormatException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid reward key {1} format. It should be a number.", filePath, rewardKey));
        } catch (IllegalArgumentException e) {
            jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid reward type {1}. Possible values: {2}", filePath, rewardSection.getString("type"), Arrays.asList(RewardType.values())));
        }
        return null;
    }
}
