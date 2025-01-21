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
        prefix = yaml.getString("prefix", "<gradient:#43F6B5:#FFFDA5>JobQuests</gradient> <dark_gray>»</dark_gray> ");
        jobLevelUp = yaml.getString("jobLevelUp", "<b><yellow>Level up</yellow></b> <dark_gray>◇</dark_gray> {0} <dark_gray>◇</dark_gray> <gray>ʟᴠʟ {1}</gray> <dark_gray>\uD83E\uDC16</dark_gray> <green>{2}</green>");
        questCompleted = yaml.getString("questCompleted", "<aqua>Quest completed</aqua> <dark_gray>◇</dark_gray> {0}");
        objectiveCompleted = yaml.getString("objectiveCompleted", "<blue>Objective completed<blue> <dark_gray>◇</dark_gray> {0}");
        questRefreshed = yaml.getString("questRefreshed", "<gray>Quest refreshed</gray> <dark_gray>◇</dark_gray> {0}");
        rewardXp = yaml.getString("rewardXp", "{0} <green>+{1} XP</green>");
        rewardMoney = yaml.getString("rewardMoney", "<gray>You earned</gray> <gold>{0}</gold>");
        objectiveKILLCompleted = yaml.getString("objectiveKILLCompleted", "<gray>Kill {0} <lang:entity.minecraft.{1}></gray>");
        objectiveBREAKCompleted = yaml.getString("objectiveBREAKCompleted", "<gray>Break {0} <lang:block.minecraft.{1}></gray>");
        playerNotFound = yaml.getString("playerNotFound", "<red>Player not found.</red>");
        jobNotFound = yaml.getString("jobNotFound", "<red>Job not found.</red>");
        questNotFound = yaml.getString("questNotFound", "<red>Quest not found.</red>");
        notANumber = yaml.getString("notANumber", "<red>Invalid argument: {0} is not a number.</red>");
        purgePlayer = yaml.getString("purgePlayer", "<green>Purged player {0} jobs: {1}.</green>");
        resetJob = yaml.getString("resetJob", "<green>Reset job {0} for player {1}.</green>");
        resetQuest = yaml.getString("resetQuest", "<green>Reset quest {0} of job {1} for player {2}.</green>");
        reload = yaml.getString("reload", "<green>Configurations reloaded.</green>");
        setLevel = yaml.getString("setLevel", "<green>Set {0} job {1} level to {2}.</green>");
        noPerm = yaml.getString("noPerm", "<red>You don't have access to this command.</red>");
        adminHelp = yaml.getStringList("adminHelp");
    }
}
