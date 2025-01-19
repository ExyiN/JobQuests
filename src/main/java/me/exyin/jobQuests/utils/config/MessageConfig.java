package me.exyin.jobQuests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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
    private String objectiveKILLDesc;
    private String objectiveBREAKDesc;
    private String reload;

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
        objectiveKILLDesc = yaml.getString("objectiveKILLDesc");
        objectiveBREAKDesc = yaml.getString("objectiveBREAKDesc");
        reload = yaml.getString("reload");
    }
}
