package me.exyin.jobQuests.listeners;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.enums.ObjectiveEventType;
import me.exyin.jobQuests.model.player.PlayerJob;
import me.exyin.jobQuests.model.player.PlayerObjective;
import me.exyin.jobQuests.model.player.PlayerQuest;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class EntityDeathListener implements Listener {
    private final JobQuests jobQuests;

    public EntityDeathListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        Player player = entityDeathEvent.getEntity().getKiller();
        if (player == null) {
            return;
        }
        if (jobQuests.getConfigManager().getWorldBlacklist().contains(player.getWorld().getName())
                || jobQuests.getConfigManager().getGameModeBlacklist().contains(player.getGameMode())) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> job.getQuests().forEach(quest -> {
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(player.getUniqueId(), job.getId());
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId());
            if (playerQuest.getCompletedDate() != null || quest.getRequiredLevel() > jobQuests.getPlayerManager().calculateJobLevel(playerJob.getXp(), 1)) {
                return;
            }
            quest.getObjectives().forEach(objective -> {
                if (objective.getObjectiveEventType() != ObjectiveEventType.KILL) {
                    return;
                }
                EntityType entityType = (EntityType) objective.getObjectiveType().getType();
                if (entityDeathEvent.getEntity().getType() != entityType) {
                    return;
                }
                PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(player.getUniqueId(), job.getId(), quest.getId(), objective.getId());
                if (playerObjective.getProgression() >= objective.getQuantity()) {
                    return;
                }
                jobQuests.getPlayerManager().incrementProgression(player.getUniqueId(), job.getId(), quest.getId(), objective.getId());
                if (playerObjective.getProgression() >= objective.getQuantity()) {
                    jobQuests.getMessageManager().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getObjectiveCompleted(), objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity())));
                    player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getObjectiveCompletionSound()), jobQuests.getConfigManager().getObjectiveCompletionSoundVolume(), jobQuests.getConfigManager().getObjectiveCompletionSoundPitch());
                }
            });
            if (jobQuests.getPlayerManager().checkQuestCompletion(player.getUniqueId(), job.getId(), quest.getId())) {
                jobQuests.getMessageManager().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getQuestCompleted(), quest.getTitle()));
                player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getQuestCompletionSound()), jobQuests.getConfigManager().getQuestCompletionSoundVolume(), jobQuests.getConfigManager().getQuestCompletionSoundPitch());
                jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId()).setCompletedDate(LocalDateTime.now());
                long oldLevel = jobQuests.getPlayerManager().calculateJobLevel(playerJob.getXp(), 1);
                jobQuests.getPlayerManager().giveRewards(player.getUniqueId(), job.getId(), quest.getId());
                long newLevel = jobQuests.getPlayerManager().calculateJobLevel(playerJob.getXp(), 1);
                if (oldLevel < newLevel) {
                    jobQuests.getMessageManager().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getJobLevelUp(), job.getName(), oldLevel, newLevel));
                    player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getJobLevelUpSound()), jobQuests.getConfigManager().getJobLevelUpSoundVolume(), jobQuests.getConfigManager().getJobLevelUpSoundPitch());
                }
            }
        }));
    }
}
