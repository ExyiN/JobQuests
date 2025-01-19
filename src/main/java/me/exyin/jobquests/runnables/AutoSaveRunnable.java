package me.exyin.jobquests.runnables;

import me.exyin.jobquests.JobQuests;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveRunnable extends BukkitRunnable {
    private final JobQuests jobQuests;

    public AutoSaveRunnable(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void run() {
        jobQuests.getServer().getOnlinePlayers().forEach(player -> jobQuests.getPlayerManager().savePlayer(player.getUniqueId()));
    }
}
