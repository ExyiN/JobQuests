package me.exyin.jobquests.runnables;

import me.exyin.jobquests.JobQuests;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardUpdateRunnable extends BukkitRunnable {
    private final JobQuests jobQuests;

    public LeaderboardUpdateRunnable(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void run() {
        jobQuests.getLeaderboardManager().updateLeaderboard();
    }
}
