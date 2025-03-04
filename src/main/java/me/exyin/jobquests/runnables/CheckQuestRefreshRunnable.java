package me.exyin.jobquests.runnables;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Quest;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
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
                            jobQuests.getPlayerManager().resetPlayerQuest(player.getUniqueId(), playerJob.getJobId(), playerQuest.getQuestId());
                            jobQuests.getMessageUtil().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getQuestRefreshed(), quest.getTitle()));
                        })));
    }
}
