package me.exyin.jobQuests;

import lombok.Getter;
import me.exyin.jobQuests.commands.JQCommands;
import me.exyin.jobQuests.listeners.PlayerListener;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.utils.JobLoader;
import me.exyin.jobQuests.utils.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;

@Getter
public final class JobQuests extends JavaPlugin {
    private Set<Job> jobs;
    private JobLoader jobLoader;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        jobLoader = new JobLoader(this);
        jobs = jobLoader.loadAllJobs();
        playerManager = new PlayerManager(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(this.getCommand("jobquests")).setExecutor(new JQCommands(this));
    }

    @Override
    public void onDisable() {

    }

    public void reloadJobs() {
        jobs.clear();
        jobs = jobLoader.loadAllJobs();
    }
}
