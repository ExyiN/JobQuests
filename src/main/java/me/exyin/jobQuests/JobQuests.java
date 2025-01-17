package me.exyin.jobQuests;

import lombok.Getter;
import me.exyin.jobQuests.commands.JQCommands;
import me.exyin.jobQuests.listeners.BlockBreakListener;
import me.exyin.jobQuests.listeners.EntityDeathListener;
import me.exyin.jobQuests.listeners.InventoryListener;
import me.exyin.jobQuests.listeners.PlayerListener;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.runnables.CheckQuestRefreshRunnable;
import me.exyin.jobQuests.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

@Getter
public final class JobQuests extends JavaPlugin {
    private ConfigManager configManager;
    private MessageConfig messageConfig;
    private MessageManager messageManager;
    private JobLoader jobLoader;
    private JobManager jobManager;
    private PlayerManager playerManager;
    private TimeUtil timeUtil;
    private GuiUtil guiUtil;
    private GuiConfig guiConfig;
    private Economy economy;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().severe("Vault dependency not found. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        configManager = new ConfigManager(this);
        messageConfig = new MessageConfig(this);
        messageManager = new MessageManager(this);
        jobLoader = new JobLoader(this);
        List<Job> jobs = jobLoader.loadAllJobs();
        jobManager = new JobManager(jobs);
        playerManager = new PlayerManager(this);
        timeUtil = new TimeUtil();
        guiUtil = new GuiUtil(this);
        guiConfig = new GuiConfig(this);

        registerListeners();
        Objects.requireNonNull(this.getCommand("jobquests")).setExecutor(new JQCommands(this));
        new CheckQuestRefreshRunnable(this).runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    public void reloadJobs() {
        List<Job> jobs = jobLoader.loadAllJobs();
        jobManager.setJobs(jobs);
    }

    public void reloadMessages() {
        messageConfig.setupValues();
    }

    public void reloadConfig() {
        configManager.setupValues();
    }

    public void reloadGuiConfig() {
        guiConfig.setupValues();
    }
}
