package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.Objective;
import me.exyin.jobquests.model.Quest;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.player.PlayerObjective;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class JQListener {
    protected final JobQuests jobQuests;

    protected JQListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    protected abstract boolean isEventNotTriggered(Player player, Cancellable event);

    protected boolean skipObjective(Job job, Quest quest, Objective objective, UUID uuid, ObjectiveEventType objectiveEventType, Translatable eventType) {
        Translatable objectiveType = objective.getObjectiveType().getType();
        PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(uuid, job.getId(), quest.getId(), objective.getId());
        return objective.getObjectiveEventType() != objectiveEventType
                || eventType != objectiveType
                || playerObjective.getProgression() >= objective.getQuantity();
    }

    protected void notifyObjectiveCompletion(Player player, Job job, Quest quest, Objective objective) {
        PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(player.getUniqueId(), job.getId(), quest.getId(), objective.getId());
        if (playerObjective.getProgression() >= objective.getQuantity()) {
            String message = MessageFormat.format(jobQuests.getMessageConfig().getObjectiveCompleted(), "<objective>");
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("objective", objective.getObjectiveType().getCompletedMessage(objective.getQuantity()));
            jobQuests.getMessageUtil().sendMessage(player, jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders));
            player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getObjectiveCompletionSound()), jobQuests.getConfigManager().getObjectiveCompletionSoundVolume(), jobQuests.getConfigManager().getObjectiveCompletionSoundPitch());
        }
    }

    protected void completeQuest(Player player, Job job, Quest quest) {
        jobQuests.getMessageUtil().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getQuestCompleted(), quest.getTitle()));
        player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getQuestCompletionSound()), jobQuests.getConfigManager().getQuestCompletionSoundVolume(), jobQuests.getConfigManager().getQuestCompletionSoundPitch());
        jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId()).setCompletedDate(LocalDateTime.now());
    }

    protected void notifyJobLevelUp(Player player, Job job, long oldLevel) {
        long newLevel = jobQuests.getPlayerManager().changeJobLevel(player.getUniqueId(), job.getId());
        if (oldLevel < newLevel) {
            jobQuests.getMessageUtil().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getJobLevelUp(), job.getName(), oldLevel, newLevel));
            player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getJobLevelUpSound()), jobQuests.getConfigManager().getJobLevelUpSoundVolume(), jobQuests.getConfigManager().getJobLevelUpSoundPitch());
        }
    }
}
