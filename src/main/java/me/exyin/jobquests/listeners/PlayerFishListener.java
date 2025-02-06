package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerQuest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Objects;

public class PlayerFishListener extends JQListener implements Listener {
    public PlayerFishListener(JobQuests jobQuests) {
        super(jobQuests);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent playerFishEvent) {
        Player player = playerFishEvent.getPlayer();
        if (isEventNotTriggered(player, playerFishEvent)) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> job.getQuests().forEach(quest -> {
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(player.getUniqueId(), job.getId());
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId());
            if (playerQuest.getCompletedDate() != null || quest.getRequiredLevel() > playerJob.getLevel()) {
                return;
            }
            quest.getObjectives().forEach(objective -> {
                if (skipObjective(job, quest, objective, player.getUniqueId(), ObjectiveEventType.FISH, ((Item) Objects.requireNonNull(playerFishEvent.getCaught())).getItemStack().getType())) {
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
        PlayerFishEvent playerFishEvent = (PlayerFishEvent) event;
        return !player.hasPermission("jobquests.use")
                || jobQuests.getConfigManager().getWorldBlacklist().contains(player.getWorld().getName())
                || jobQuests.getConfigManager().getGameModeBlacklist().contains(player.getGameMode())
                || playerFishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH
                || playerFishEvent.getCaught() == null
                || !(playerFishEvent.getCaught() instanceof Item);
    }
}
