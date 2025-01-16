package me.exyin.jobQuests.listeners;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.enums.ObjectiveEventType;
import me.exyin.jobQuests.model.player.PlayerObjective;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.time.LocalDateTime;

public class EntityDeathListener implements Listener {
    private final JobQuests jobQuests;

    public EntityDeathListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        Entity entity = entityDeathEvent.getDamageSource().getCausingEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> {
            job.getQuests().forEach(quest -> {
                if (jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId()).getCompletedDate() != null) {
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
                });
                if(jobQuests.getPlayerManager().checkQuestCompletion(player.getUniqueId(), job.getId(), quest.getId())) {
                    jobQuests.getPlayerManager().giveRewards(player.getUniqueId(), job.getId(), quest.getId());
                    jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId()).setCompletedDate(LocalDateTime.now());
                }
            });
        });
    }
}
