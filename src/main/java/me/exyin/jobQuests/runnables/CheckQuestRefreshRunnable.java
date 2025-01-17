package me.exyin.jobQuests.runnables;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.Quest;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

public class CheckQuestRefreshRunnable extends BukkitRunnable {
    private final JobQuests jobQuests;

    public CheckQuestRefreshRunnable(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void run() {
        jobQuests.getServer().getOnlinePlayers().forEach(player ->
                jobQuests.getPlayerManager().getJQPlayer(player.getUniqueId()).getPlayerJobs().forEach(playerJob ->
                        playerJob.getPlayerQuests().forEach(playerQuest -> {
                            if (playerQuest.getCompletedDate() == null) {
                                return;
                            }
                            Quest quest = jobQuests.getJobManager().getQuest(playerJob.getJobId(), playerQuest.getQuestId());
                            if (LocalDateTime.now().isBefore(jobQuests.getTimeUtil().getRefreshDate(playerQuest.getCompletedDate(), quest.getRefreshTime()))) {
                                return;
                            }
                            jobQuests.getPlayerManager().refreshPlayerQuest(player.getUniqueId(), playerJob.getJobId(), playerQuest.getQuestId());
                        })));
    }
}
