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
    private String questItemName;
    private String questItemObjective;
    private String questItemCompletedObjective;
    private String questItemRefreshTime;
    private boolean questItemEnchanted;
    private Material lockedQuestItemMaterial;
    private String lockedQuestItemName;
    private String lockedQuestItemObjective;
    private String lockedQuestItemRefreshTime;
    private boolean lockedQuestItemEnchanted;
    private Material completedQuestItemMaterial;
    private String completedQuestItemName;
    private String completedQuestItemObjective;
    private String completedQuestItemRefreshTime;
    private boolean completedQuestItemEnchanted;
    private Map<ObjectiveEventType, String> questGuiObjective;
    private Map<RewardType, String> questGuiReward;
    private int questGuiBackButtonSlot;
    private Material questGuiBackButtonMaterial;
    private String questGuiBackButtonName;
    private List<String> questGuiBackButtonLore;
    private boolean questGuiBackButtonEnchanted;
    private int questGuiPrevPageButtonSlot;
    private Material questGuiPrevPageButtonMaterial;
    private String questGuiPrevPageButtonName;
    private List<String> questGuiPrevPageButtonLore;
    private boolean questGuiPrevPageButtonEnchanted;
    private int questGuiNextPageButtonSlot;
    private Material questGuiNextPageButtonMaterial;
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
        jobGuiTitle = yaml.getString("title");
        jobGuiRows = yaml.getInt("rows") > 6 || yaml.getInt("rows") < 1 ? 6 : yaml.getInt("rows");
        jobGuiEmpty = Material.valueOf(yaml.getString("empty"));
        jobItemName = yaml.getString("jobItem.name");
        jobItemLore = yaml.getStringList("jobItem.lore");
        jobItemEnchanted = yaml.getBoolean("jobItem.enchanted");
        ConfigurationSection jobSlotSection = yaml.getConfigurationSection("jobSlot");
        if (jobSlotSection == null) {
            return;
        }
        jobGuiSlot = new HashMap<>();
        for (String slotKey : jobSlotSection.getKeys(false)) {
            String jobId = jobSlotSection.getString(slotKey);
            if (jobQuests.getJobManager().getJobs().stream().filter(job -> job.getId().equals(jobId)).toList().isEmpty()) {
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
        questGuiTitle = yaml.getString("title");
        questGuiRows = yaml.getInt("rows") > 1 && yaml.getInt("rows") < 7 ? yaml.getInt("rows") : 6;
        questGuiEmpty = Material.valueOf(yaml.getString("empty"));

        ConfigurationSection questItemSection = yaml.getConfigurationSection("questItem");
        if (questItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing questItem section.", questGuiFile.getPath()));
            return;
        }
        questItemMaterial = Material.valueOf(questItemSection.getString("material"));
        questItemName = questItemSection.getString("name");
        questItemObjective = questItemSection.getString("objective");
        questItemCompletedObjective = questItemSection.getString("completedObjective");
        questItemRefreshTime = questItemSection.getString("refreshTime");
        questItemEnchanted = questItemSection.getBoolean("enchanted");

        ConfigurationSection lockedQuestItemSection = yaml.getConfigurationSection("lockedQuestItem");
        if (lockedQuestItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing lockedQuestItem section.", questGuiFile.getPath()));
            return;
        }
        lockedQuestItemMaterial = Material.valueOf(lockedQuestItemSection.getString("material"));
        lockedQuestItemName = lockedQuestItemSection.getString("name");
        lockedQuestItemObjective = lockedQuestItemSection.getString("objective");
        lockedQuestItemRefreshTime = lockedQuestItemSection.getString("refreshTime");
        lockedQuestItemEnchanted = lockedQuestItemSection.getBoolean("enchanted");

        ConfigurationSection completedQuestItemSection = yaml.getConfigurationSection("completedQuestItem");
        if (completedQuestItemSection == null) {
            jobQuests.getLogger().severe(MessageFormat.format("In file {0}: Missing completedQuestItem section.", questGuiFile.getPath()));
            return;
        }
        completedQuestItemMaterial = Material.valueOf(completedQuestItemSection.getString("material"));
        completedQuestItemName = completedQuestItemSection.getString("name");
        completedQuestItemObjective = completedQuestItemSection.getString("objective");
        completedQuestItemRefreshTime = completedQuestItemSection.getString("refreshTime");
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
                questGuiObjective.put(objectiveEventType, objectiveSection.getString(objectiveEventTypeKey));
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
                questGuiReward.put(rewardType, rewardSection.getString(rewardTypeKey));
            } catch (IllegalArgumentException e) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Invalid reward type {1}. Possible values: {2}", questGuiFile.getPath(), rewardTypeKey, Arrays.asList(ObjectiveEventType.values())));
            }
        }

        questGuiBackButtonSlot = yaml.getInt("footer.backButton.slot");
        questGuiBackButtonMaterial = Material.valueOf(yaml.getString("footer.backButton.material"));
        questGuiBackButtonName = yaml.getString("footer.backButton.name");
        questGuiBackButtonLore = yaml.getStringList("footer.backButton.lore");
        questGuiBackButtonEnchanted = yaml.getBoolean("footer.backButton.enchanted");

        questGuiPrevPageButtonSlot = yaml.getInt("footer.prevPageButton.slot");
        questGuiPrevPageButtonMaterial = Material.valueOf(yaml.getString("footer.prevPageButton.material"));
        questGuiPrevPageButtonName = yaml.getString("footer.prevPageButton.name");
        questGuiPrevPageButtonLore = yaml.getStringList("footer.prevPageButton.lore");
        questGuiPrevPageButtonEnchanted = yaml.getBoolean("footer.prevPageButton.enchanted");

        questGuiNextPageButtonSlot = yaml.getInt("footer.nextPageButton.slot");
        questGuiNextPageButtonMaterial = Material.valueOf(yaml.getString("footer.nextPageButton.material"));
        questGuiNextPageButtonName = yaml.getString("footer.nextPageButton.name");
        questGuiNextPageButtonLore = yaml.getStringList("footer.nextPageButton.lore");
        questGuiNextPageButtonEnchanted = yaml.getBoolean("footer.nextPageButton.enchanted");

        questGuiYear = yaml.getString("time.year");
        questGuiMonth = yaml.getString("time.month");
        questGuiDay = yaml.getString("time.day");
        questGuiHour = yaml.getString("time.hour");
        questGuiMinute = yaml.getString("time.minute");
        questGuiSecond = yaml.getString("time.second");
    }
}
