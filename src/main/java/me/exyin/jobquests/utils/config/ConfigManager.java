package me.exyin.jobquests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

@Getter
public class ConfigManager {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;

    private double jobXpLevelUpRequirementBase;
    private List<String> worldBlacklist;
    private List<GameMode> gameModeBlacklist;
    private long autoSavePeriod;
    private String jobLevelUpSound;
    private float jobLevelUpSoundVolume;
    private float jobLevelUpSoundPitch;
    private String questCompletionSound;
    private float questCompletionSoundVolume;
    private float questCompletionSoundPitch;
    private String objectiveCompletionSound;
    private float objectiveCompletionSoundVolume;
    private float objectiveCompletionSoundPitch;

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
        jobXpLevelUpRequirementBase = yaml.getDouble("job.xpLevelUpRequirement.base", 1000.0);
        worldBlacklist = yaml.getStringList("worldBlacklist");
        gameModeBlacklist = yaml.getStringList("gameModeBlacklist").stream().map(GameMode::valueOf).toList();
        autoSavePeriod = yaml.getLong("autoSavePeriod", 1200);
        jobLevelUpSound = yaml.getString("sounds.jobLevelUp.sound", "ENTITY_PLAYER_LEVELUP");
        jobLevelUpSoundVolume = (float) yaml.getDouble("sounds.jobLevelUp.volume", 1.0);
        jobLevelUpSoundPitch = (float) yaml.getDouble("sounds.jobLevelUp.pitch", 0.8);
        questCompletionSound = yaml.getString("sounds.questCompletion.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
        questCompletionSoundVolume = (float) yaml.getDouble("sounds.questCompletion.volume", 1.0);
        questCompletionSoundPitch = (float) yaml.getDouble("sounds.questCompletion.pitch", 0.8);
        objectiveCompletionSound = yaml.getString("sounds.objectiveCompletion.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
        objectiveCompletionSoundVolume = (float) yaml.getDouble("sounds.objectiveCompletion.volume", 1.0);
        objectiveCompletionSoundPitch = (float) yaml.getDouble("sounds.objectiveCompletion.pitch", 0.8);
    }
}
