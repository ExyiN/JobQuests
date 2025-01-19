package me.exyin.jobQuests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

@Getter
public class ConfigManager {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;

    private double jobXpLevelUpRequirementBase;
    private double jobXpLevelUpRequirementMultiplier;
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
        jobXpLevelUpRequirementBase = yaml.getDouble("job.xpLevelUpRequirement.base");
        jobXpLevelUpRequirementMultiplier = yaml.getDouble("job.xpLevelUpRequirement.multiplier");
        worldBlacklist = yaml.getStringList("worldBlacklist");
        gameModeBlacklist = yaml.getStringList("gameModeBlacklist").stream().map(GameMode::valueOf).toList();
        autoSavePeriod = yaml.getLong("autoSavePeriod");
        jobLevelUpSound = yaml.getString("sounds.jobLevelUp.sound");
        jobLevelUpSoundVolume = (float) yaml.getDouble("sounds.jobLevelUp.volume");
        jobLevelUpSoundPitch = (float) yaml.getDouble("sounds.jobLevelUp.pitch");
        questCompletionSound = yaml.getString("sounds.questCompletion.sound");
        questCompletionSoundVolume = (float) yaml.getDouble("sounds.questCompletion.volume");
        questCompletionSoundPitch = (float) yaml.getDouble("sounds.questCompletion.pitch");
        objectiveCompletionSound = yaml.getString("sounds.objectiveCompletion.sound");
        objectiveCompletionSoundVolume = (float) yaml.getDouble("sounds.objectiveCompletion.volume");
        objectiveCompletionSoundPitch = (float) yaml.getDouble("sounds.objectiveCompletion.pitch");
    }
}
