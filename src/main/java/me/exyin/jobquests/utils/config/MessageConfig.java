package me.exyin.jobquests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

@Getter
public class MessageConfig {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;
    private String prefix;
    private String jobLevelUp;
    private String questCompleted;
    private String objectiveCompleted;
    private String questRefreshed;
    private String rewardXp;
    private String rewardMoney;
    private String objectiveKILLCompleted;
    private String objectiveBREAKCompleted;
    private String playerNotFound;
    private String jobNotFound;
    private String questNotFound;
    private String notANumber;
    private String purgePlayer;
    private String resetJob;
    private String resetQuest;
    private String reload;
    private String setLevel;
    private String noPerm;
    private List<String> adminHelp;

    public MessageConfig(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        setupValues();
    }

    public void setupValues() {
        File messageFile = new File(jobQuests.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            jobQuests.saveResource("messages.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(messageFile);
        prefix = yaml.getString("prefix");
        jobLevelUp = yaml.getString("jobLevelUp");
        questCompleted = yaml.getString("questCompleted");
        objectiveCompleted = yaml.getString("objectiveCompleted");
        questRefreshed = yaml.getString("questRefreshed");
        rewardXp = yaml.getString("rewardXp");
        rewardMoney = yaml.getString("rewardMoney");
        objectiveKILLCompleted = yaml.getString("objectiveKILLCompleted");
        objectiveBREAKCompleted = yaml.getString("objectiveBREAKCompleted");
        playerNotFound = yaml.getString("playerNotFound");
        jobNotFound = yaml.getString("jobNotFound");
        questNotFound = yaml.getString("questNotFound");
        notANumber = yaml.getString("notANumber");
        purgePlayer = yaml.getString("purgePlayer");
        resetJob = yaml.getString("resetJob");
        resetQuest = yaml.getString("resetQuest");
        reload = yaml.getString("reload");
        setLevel = yaml.getString("setLevel");
        noPerm = yaml.getString("noPerm");
        adminHelp = yaml.getStringList("adminHelp");
    }
}
