package me.exyin.jobQuests.utils;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class ConfigManager {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;
    private String questCompletionSound;
    private int questCompletionSoundVolume;
    private int questCompletionSoundPitch;
    private String jobLevelUpSound;
    private int jobLevelUpSoundVolume;
    private int jobLevelUpSoundPitch;

    public ConfigManager(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        setupValues();
    }

    public void setupValues() {
        File messageFile = new File(jobQuests.getDataFolder(), "config.yml");
        if (!messageFile.exists()) {
            jobQuests.saveResource("config.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(messageFile);
        questCompletionSound = yaml.getString("sounds.questCompletion.sound");
        questCompletionSoundVolume = yaml.getInt("sounds.questCompletion.volume");
        questCompletionSoundPitch = yaml.getInt("sounds.questCompletion.pitch");
        jobLevelUpSound = yaml.getString("sounds.jobLevelUp.sound");
        jobLevelUpSoundVolume = yaml.getInt("sounds.jobLevelUp.volume");
        jobLevelUpSoundPitch = yaml.getInt("sounds.jobLevelUp.pitch");
    }
}
