package me.exyin.jobQuests;

import lombok.Getter;
import me.exyin.jobQuests.commands.JQCommands;
import me.exyin.jobQuests.listeners.EntityDeathListener;
import me.exyin.jobQuests.listeners.PlayerListener;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;

@Getter
public final class JobQuests extends JavaPlugin {
    private JobLoader jobLoader;
    private JobManager jobManager;
    private PlayerManager playerManager;
    private MessageConfig messageConfig;
    private MessageManager  messageManager;
    private Economy economy;

    @Override
    public void onEnable() {
        if(!setupEconomy()) {
            this.getLogger().severe("Vault dependency not found. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        jobLoader = new JobLoader(this);
        Set<Job> jobs = jobLoader.loadAllJobs();
        jobManager = new JobManager(jobs);
        playerManager = new PlayerManager(this);
        messageConfig = new MessageConfig(this);
        messageManager = new MessageManager(this);

        registerListeners();
        Objects.requireNonNull(this.getCommand("jobquests")).setExecutor(new JQCommands(this));
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
        return economy != null;
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
    }

    public void reloadJobs() {
        Set<Job> jobs = jobLoader.loadAllJobs();
        jobManager.setJobs(jobs);
    }

    public void reloadMessages() {
        messageConfig.setupValues();
    }
}
