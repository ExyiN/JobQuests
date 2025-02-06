package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerQuest;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends JQListener implements Listener {
    public BlockBreakListener(JobQuests jobQuests) {
        super(jobQuests);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (isEventNotTriggered(player, blockBreakEvent)) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> job.getQuests().forEach(quest -> {
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(player.getUniqueId(), job.getId());
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId());
            if (playerQuest.getCompletedDate() != null || quest.getRequiredLevel() > playerJob.getLevel()) {
                return;
            }
            quest.getObjectives().forEach(objective -> {
                if (skipObjective(job, quest, objective, player.getUniqueId(), ObjectiveEventType.BREAK, blockBreakEvent.getBlock().getType())) {
                    return;
                }
                jobQuests.getPlayerManager().incrementProgression(player.getUniqueId(), job.getId(), quest.getId(), objective.getId());
                notifyObjectiveCompletion(player, job, quest, objective);
            });
            if (jobQuests.getPlayerManager().checkQuestCompletion(player.getUniqueId(), job.getId(), quest.getId())) {
                long oldLevel = playerJob.getLevel();
                completeQuest(player, job, quest);
                jobQuests.getPlayerManager().giveRewards(player.getUniqueId(), job.getId(), quest.getId());
                notifyJobLevelUp(player, job, oldLevel);
            }
        }));
    }

    @Override
    protected boolean isEventNotTriggered(Player player, Cancellable event) {
        BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
        return !player.hasPermission("jobquests.use")
                || jobQuests.getConfigManager().getWorldBlacklist().contains(player.getWorld().getName())
                || jobQuests.getConfigManager().getGameModeBlacklist().contains(player.getGameMode())
                || (blockBreakEvent.getBlock().getBlockData() instanceof Ageable crop && crop.getAge() < crop.getMaximumAge());
    }
}
