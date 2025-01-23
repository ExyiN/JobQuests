package me.exyin.jobquests;

import lombok.Getter;
import me.exyin.jobquests.commands.JQCommands;
import me.exyin.jobquests.listeners.BlockBreakListener;
import me.exyin.jobquests.listeners.EntityDeathListener;
import me.exyin.jobquests.listeners.InventoryListener;
import me.exyin.jobquests.listeners.PlayerListener;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.runnables.AutoSaveRunnable;
import me.exyin.jobquests.runnables.CheckQuestRefreshRunnable;
import me.exyin.jobquests.runnables.LeaderboardUpdateRunnable;
import me.exyin.jobquests.utils.*;
import me.exyin.jobquests.utils.config.ConfigManager;
import me.exyin.jobquests.utils.config.GuiConfig;
import me.exyin.jobquests.utils.config.MessageConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;

@Getter
public final class JobQuests extends JavaPlugin {
    private ConfigManager configManager;
    private MessageConfig messageConfig;
    private MessageUtil messageUtil;
    private JobLoader jobLoader;
    private JobManager jobManager;
    private PlayerManager playerManager;
    private LeaderboardManager leaderboardManager;
    private TimeUtil timeUtil;
    private GuiUtil guiUtil;
    private GuiConfig guiConfig;
    private Economy economy;
    private BukkitTask autoSaveRunnable;
    private BukkitTask leaderboardUpdateRunnable;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().severe("Vault dependency not found. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        configManager = new ConfigManager(this);
        messageConfig = new MessageConfig(this);
        messageUtil = new MessageUtil(this);
        jobLoader = new JobLoader(this);
        List<Job> jobs = jobLoader.loadAllJobs();
        jobManager = new JobManager(jobs);
        playerManager = new PlayerManager(this);
        leaderboardManager = new LeaderboardManager(this);
        timeUtil = new TimeUtil(this);
        guiUtil = new GuiUtil(this);
        guiConfig = new GuiConfig(this);

        registerListeners();
        Objects.requireNonNull(this.getCommand("jobquests")).setExecutor(new JQCommands(this));
        launchRunnables();
    }

    @Override
    public void onDisable() {
        this.getServer().getOnlinePlayers().forEach(player -> playerManager.savePlayer(player.getUniqueId()));
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
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

    private void launchRunnables() {
        new CheckQuestRefreshRunnable(this).runTaskTimer(this, 0L, 20L);
        leaderboardUpdateRunnable =  new LeaderboardUpdateRunnable(this).runTaskTimer(this, 0L, configManager.getRefreshLeaderboardPeriod());
        autoSaveRunnable = new AutoSaveRunnable(this).runTaskTimer(this, 0L, configManager.getAutoSavePeriod());
    }

    public void reloadPlugin() {
        List<Job> jobs = jobLoader.loadAllJobs();
        jobManager.setJobs(jobs);
        configManager.setupValues();
        messageConfig.setupValues();
        guiConfig.setupValues();
        autoSaveRunnable.cancel();
        leaderboardUpdateRunnable.cancel();
        autoSaveRunnable = new AutoSaveRunnable(this).runTaskTimer(this, 0L, configManager.getAutoSavePeriod());
        leaderboardUpdateRunnable =  new LeaderboardUpdateRunnable(this).runTaskTimer(this, 0L, configManager.getRefreshLeaderboardPeriod());
    }
}
