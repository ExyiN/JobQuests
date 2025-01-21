package me.exyin.jobquests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.enums.RewardType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

@Getter
public class GuiConfig {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;

    private String jobGuiTitle;
    private int jobGuiRows;
    private Material jobGuiEmpty;
    private String jobItemName;
    private List<String> jobItemLore;
    private boolean jobItemEnchanted;
    private Map<Integer, String> jobGuiSlot;

    private String questGuiTitle;
    private int questGuiRows;
    private Material questGuiEmpty;
    private Material questItemMaterial;
    private int questItemCustomModelData;
    private String questItemName;
    private String questItemObjective;
    private String questItemCompletedObjective;
    private String questItemRefreshTime;
    private boolean questItemEnchanted;
    private Material lockedQuestItemMaterial;
    private int lockedQuestItemCustomModelData;
    private String lockedQuestItemName;
    private String lockedQuestItemObjective;
    private String lockedQuestItemRefreshTime;
    private boolean lockedQuestItemEnchanted;
    private Material completedQuestItemMaterial;
    private int completedQuestItemCustomModelData;
    private String completedQuestItemName;
    private String completedQuestItemObjective;
    private String completedQuestItemRefreshTime;
    private boolean completedQuestItemEnchanted;
    private Map<ObjectiveEventType, String> questGuiObjective;
    private Map<RewardType, String> questGuiReward;
    private int questGuiBackButtonSlot;
    private Material questGuiBackButtonMaterial;
    private int questGuiBackButtonCustomModelData;
    private String questGuiBackButtonName;
    private List<String> questGuiBackButtonLore;
    private boolean questGuiBackButtonEnchanted;
    private int questGuiPrevPageButtonSlot;
    private Material questGuiPrevPageButtonMaterial;
    private int questGuiPrevPageButtonCustomModelData;
    private String questGuiPrevPageButtonName;
    private List<String> questGuiPrevPageButtonLore;
    private boolean questGuiPrevPageButtonEnchanted;
    private int questGuiNextPageButtonSlot;
    private Material questGuiNextPageButtonMaterial;
    private int questGuiNextPageButtonCustomModelData;
    private String questGuiNextPageButtonName;
    private List<String> questGuiNextPageButtonLore;
    private boolean questGuiNextPageButtonEnchanted;
    private String questGuiYear;
    private String questGuiMonth;
    private String questGuiDay;
    private String questGuiHour;
    private String questGuiMinute;
    private String questGuiSecond;


    public GuiConfig(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        setupValues();
    }

    public void setupValues() {
        setupJobGuiValues();
        setupQuestGuiValues();
    }

    private void setupJobGuiValues() {
        File jobGuiFile = new File(jobQuests.getDataFolder(), "gui" + File.separator + "jobsGui.yml");
        if (!jobGuiFile.exists()) {
            jobQuests.saveResource("gui" + File.separator + "jobsGui.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(jobGuiFile);
        jobGuiTitle = yaml.getString("title", "JobQuests");
        jobGuiRows = yaml.getInt("rows") > 6 || yaml.getInt("rows") < 1 ? 6 : yaml.getInt("rows");
        jobGuiEmpty = Material.valueOf(yaml.getString("empty", "AIR"));
        jobItemName = yaml.getString("jobItem.name", "{0} <dark_gray>◇</dark_gray> <gray>ʟᴠʟ</gray> <yellow>{1}</yellow>");
        jobItemLore = yaml.getStringList("jobItem.lore");
        jobItemEnchanted = yaml.getBoolean("jobItem.enchanted");
        ConfigurationSection jobSlotSection = yaml.getConfigurationSection("jobSlot");
        if (jobSlotSection == null) {
            return;
        }
        jobGuiSlot = new HashMap<>();
        for (String slotKey : jobSlotSection.getKeys(false)) {
            String jobId = jobSlotSection.getString(slotKey, "");
            if (!jobQuests.getJobManager().existsJob(jobId)) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Job {1} not found.", jobGuiFile.getPath(), jobId));
                continue;
            }
            try {
                int slot = Integer.parseInt(slotKey);
                jobGuiSlot.put(slot, jobId);
            } catch (NumberFormatException e) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid slot number {1}.", jobGuiFile.getPath(), slotKey));
            }

        }
    }

    private void setupQuestGuiValues() {
        File questGuiFile = new File(jobQuests.getDataFolder(), "gui" + File.separator + "questsGui.yml");
        if (!questGuiFile.exists()) {
            jobQuests.saveResource("gui" + File.separator + "questsGui.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(questGuiFile);
        questGuiTitle = yaml.getString("title", "{0}");
        questGuiRows = yaml.getInt("rows") > 1 && yaml.getInt("rows") < 7 ? yaml.getInt("rows") : 6;
        questGuiEmpty = Material.valueOf(yaml.getString("empty", "AIR"));

        ConfigurationSection questItemSection = yaml.getConfigurationSection("questItem");
        if (questItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing questItem section.", questGuiFile.getPath()));
            return;
        }
        questItemMaterial = Material.valueOf(questItemSection.getString("material", "WRITABLE_BOOK"));
        questItemCustomModelData = questItemSection.getInt("customModelData", -1);
        questItemName = questItemSection.getString("name", "{0} <dark_gray>◇</dark_gray> <gray>ʟᴠʟ</gray> <yellow>{1}</yellow>");
        questItemObjective = questItemSection.getString("objective", "<dark_gray>•</dark_gray> <gray>{0}</gray>");
        questItemCompletedObjective = questItemSection.getString("completedObjective", "<dark_gray>•</dark_gray> <green>{0} ✔</green>");
        questItemRefreshTime = questItemSection.getString("refreshTime", "<gray>Refresh time <dark_gray>»</dark_gray> <gold>{0}</gold>");
        questItemEnchanted = questItemSection.getBoolean("enchanted");

        ConfigurationSection lockedQuestItemSection = yaml.getConfigurationSection("lockedQuestItem");
        if (lockedQuestItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing lockedQuestItem section.", questGuiFile.getPath()));
            return;
        }
        lockedQuestItemMaterial = Material.valueOf(lockedQuestItemSection.getString("material", "BOOK"));
        lockedQuestItemCustomModelData = lockedQuestItemSection.getInt("customModelData", -1);
        lockedQuestItemName = lockedQuestItemSection.getString("name", "{0} <dark_gray>◇</dark_gray> <gray>ʟᴠʟ</gray> <red>{1} \uD83D\uDD12</red>");
        lockedQuestItemObjective = lockedQuestItemSection.getString("objective", "<dark_gray>•</dark_gray> <gray>{0}</gray>");
        lockedQuestItemRefreshTime = lockedQuestItemSection.getString("refreshTime", "<gray>Refresh time <dark_gray>»</dark_gray> <gold>{0}</gold>");
        lockedQuestItemEnchanted = lockedQuestItemSection.getBoolean("enchanted");

        ConfigurationSection completedQuestItemSection = yaml.getConfigurationSection("completedQuestItem");
        if (completedQuestItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing completedQuestItem section.", questGuiFile.getPath()));
            return;
        }
        completedQuestItemMaterial = Material.valueOf(completedQuestItemSection.getString("material", "WRITTEN_BOOK"));
        completedQuestItemCustomModelData = completedQuestItemSection.getInt("customModelData", -1);
        completedQuestItemName = completedQuestItemSection.getString("name", "{0} <dark_gray>◇</dark_gray> <gray>ʟᴠʟ</gray> <yellow>{1}</yellow> <green>✔</green>");
        completedQuestItemObjective = completedQuestItemSection.getString("objective", "<dark_gray>•</dark_gray> <green>{0} ✔</green>");
        completedQuestItemRefreshTime = completedQuestItemSection.getString("refreshTime", "<gray>Refresh in <dark_gray>»</dark_gray> <red>{0}</red>");
        completedQuestItemEnchanted = completedQuestItemSection.getBoolean("enchanted");

        ConfigurationSection objectiveSection = yaml.getConfigurationSection("objective");
        if (objectiveSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing objective section.", questGuiFile.getPath()));
            return;
        }
        questGuiObjective = new EnumMap<>(ObjectiveEventType.class);
        for (String objectiveEventTypeKey : objectiveSection.getKeys(false)) {
            try {
                ObjectiveEventType objectiveEventType = ObjectiveEventType.valueOf(objectiveEventTypeKey.toUpperCase());
                questGuiObjective.put(objectiveEventType, objectiveSection.getString(objectiveEventTypeKey, "??? {0}/{1} {2}"));
            } catch (IllegalArgumentException e) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid objective event type {1}. Possible values: {2}", questGuiFile.getPath(), objectiveEventTypeKey, Arrays.asList(ObjectiveEventType.values())));
            }
        }

        ConfigurationSection rewardSection = yaml.getConfigurationSection("reward");
        if (rewardSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing reward section.", questGuiFile.getPath()));
            return;
        }
        questGuiReward = new EnumMap<>(RewardType.class);
        for (String rewardTypeKey : rewardSection.getKeys(false)) {
            try {
                RewardType rewardType = RewardType.valueOf(rewardTypeKey.toUpperCase());
                questGuiReward.put(rewardType, rewardSection.getString(rewardTypeKey, "<dark_gray>»</dark_gray> <red>+{0} ???</red>"));
            } catch (IllegalArgumentException e) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid reward type {1}. Possible values: {2}", questGuiFile.getPath(), rewardTypeKey, Arrays.asList(ObjectiveEventType.values())));
            }
        }

        questGuiBackButtonSlot = yaml.getInt("footer.backButton.slot", 4);
        questGuiBackButtonMaterial = Material.valueOf(yaml.getString("footer.backButton.material", "ARROW"));
        questGuiBackButtonCustomModelData = yaml.getInt("footer.backButton.customModelData", -1);
        questGuiBackButtonName = yaml.getString("footer.backButton.name", "<gray>⮪ Back</gray>");
        questGuiBackButtonLore = yaml.getStringList("footer.backButton.lore");
        questGuiBackButtonEnchanted = yaml.getBoolean("footer.backButton.enchanted");

        questGuiPrevPageButtonSlot = yaml.getInt("footer.prevPageButton.slot", 3);
        questGuiPrevPageButtonMaterial = Material.valueOf(yaml.getString("footer.prevPageButton.material", "PAPER"));
        questGuiPrevPageButtonCustomModelData = yaml.getInt("footer.prevPageButton.customModelData", -1);
        questGuiPrevPageButtonName = yaml.getString("footer.prevPageButton.name", "<gray>⮪ Previous page</gray>");
        questGuiPrevPageButtonLore = yaml.getStringList("footer.prevPageButton.lore");
        questGuiPrevPageButtonEnchanted = yaml.getBoolean("footer.prevPageButton.enchanted");

        questGuiNextPageButtonSlot = yaml.getInt("footer.nextPageButton.slot", 5);
        questGuiNextPageButtonMaterial = Material.valueOf(yaml.getString("footer.nextPageButton.material", "PAPER"));
        questGuiNextPageButtonCustomModelData = yaml.getInt("footer.nextPageButton.customModelData", -1);
        questGuiNextPageButtonName = yaml.getString("footer.nextPageButton.name", "<gray>⮫ Next page</gray>");
        questGuiNextPageButtonLore = yaml.getStringList("footer.nextPageButton.lore");
        questGuiNextPageButtonEnchanted = yaml.getBoolean("footer.nextPageButton.enchanted");

        questGuiYear = yaml.getString("time.year", "y");
        questGuiMonth = yaml.getString("time.month", "M");
        questGuiDay = yaml.getString("time.day", "d");
        questGuiHour = yaml.getString("time.hour", "h");
        questGuiMinute = yaml.getString("time.minute", "m");
        questGuiSecond = yaml.getString("time.second", "s");
    }
}
