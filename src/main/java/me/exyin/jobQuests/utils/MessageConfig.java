package me.exyin.jobQuests.utils;

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
    private String questCompleted;
    private String rewardXp;
    private String rewardMoney;

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
        questCompleted = yaml.getString("questCompleted");
        rewardXp = yaml.getString("rewardXp");
        rewardMoney = yaml.getString("rewardMoney");
    }
}
