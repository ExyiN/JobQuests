package me.exyin.jobQuests;

import lombok.Getter;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.utils.JobLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

@Getter
public final class JobQuests extends JavaPlugin {
    private Set<Job> jobs;

    @Override
    public void onEnable() {
        JobLoader jobLoader = new JobLoader(this);
        jobs = jobLoader.loadAllJobs();
        this.getLogger().info(jobs.toString());
    }

    @Override
    public void onDisable() {

    }
}
