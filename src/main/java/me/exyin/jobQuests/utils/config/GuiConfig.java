package me.exyin.jobQuests.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.exyin.jobQuests.JobQuests;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GuiConfig {
    @Getter(AccessLevel.NONE)
    private final JobQuests jobQuests;
    private String jobTitle;
    private int jobRows;
    private Material jobEmpty;
    private String jobName;
    private List<String> jobLore;
    private boolean jobItemEnchanted;
    private Map<String, Integer> jobSlot;

    public GuiConfig(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        setupValues();
    }

    public void setupValues() {
        File jobGuiFile = new File(jobQuests.getDataFolder(), "gui" + File.separator + "jobGui.yml");
        if (!jobGuiFile.exists()) {
            jobQuests.saveResource("gui" + File.separator + "jobGui.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(jobGuiFile);
        jobTitle = yaml.getString("title");
        jobRows = yaml.getInt("rows") > 6 || yaml.getInt("rows") < 1 ? 6 : yaml.getInt("rows");
        jobEmpty = Material.valueOf(yaml.getString("empty"));
        jobName = yaml.getString("jobItem.name");
        jobLore = yaml.getStringList("jobItem.lore");
        jobItemEnchanted = yaml.getBoolean("jobItem.enchanted");
        ConfigurationSection jobSlotSection = yaml.getConfigurationSection("jobSlot");
        if (jobSlotSection == null) {
            return;
        }
        jobSlot = new HashMap<>();
        for (String jobId : jobSlotSection.getKeys(false)) {
            if (jobQuests.getJobManager().getJobs().stream().filter(job -> job.getId().equals(jobId)).toList().isEmpty()) {
                jobQuests.getLogger().warning(MessageFormat.format("In file {0}: Job {1} not found.", jobGuiFile.getPath(), jobId));
                continue;
            }
            jobSlot.put(jobId, jobSlotSection.getInt(jobId));
        }
    }
}
