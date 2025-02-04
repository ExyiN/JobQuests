package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerObjective;
import me.exyin.jobquests.model.player.PlayerQuest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CraftItemListener implements Listener {
    private final JobQuests jobQuests;

    public CraftItemListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent craftItemEvent) {
        Player player = (Player) craftItemEvent.getWhoClicked();
        if (!player.hasPermission("jobquests.use")
                || jobQuests.getConfigManager().getWorldBlacklist().contains(player.getWorld().getName())
                || jobQuests.getConfigManager().getGameModeBlacklist().contains(player.getGameMode())
                || craftItemEvent.getAction() == InventoryAction.NOTHING
                || getCraftedAmount(craftItemEvent) == 0) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> job.getQuests().forEach(quest -> {
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(player.getUniqueId(), job.getId());
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId());
            if (playerQuest.getCompletedDate() != null || quest.getRequiredLevel() > playerJob.getLevel()) {
                return;
            }
            quest.getObjectives().forEach(objective -> {
                if (objective.getObjectiveEventType() != ObjectiveEventType.CRAFT) {
                    return;
                }
                Material material = (Material) objective.getObjectiveType().getType();
                if (craftItemEvent.getRecipe().getResult().getType() != material) {
                    return;
                }
                PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(player.getUniqueId(), job.getId(), quest.getId(), objective.getId());
                if (playerObjective.getProgression() >= objective.getQuantity()) {
                    return;
                }
                jobQuests.getPlayerManager().incrementProgression(player.getUniqueId(), job.getId(), quest.getId(), objective.getId(), getCraftedAmount(craftItemEvent));
                if (playerObjective.getProgression() >= objective.getQuantity()) {
                    String message = MessageFormat.format(jobQuests.getMessageConfig().getObjectiveCompleted(), "<objective>");
                    Map<String, Component> placeholders = new HashMap<>();
                    placeholders.put("objective", objective.getObjectiveType().getCompletedMessage(objective.getQuantity()));
                    jobQuests.getMessageUtil().sendMessage(player, jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders));
                    player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getObjectiveCompletionSound()), jobQuests.getConfigManager().getObjectiveCompletionSoundVolume(), jobQuests.getConfigManager().getObjectiveCompletionSoundPitch());
                }
            });
            if (jobQuests.getPlayerManager().checkQuestCompletion(player.getUniqueId(), job.getId(), quest.getId())) {
                jobQuests.getMessageUtil().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getQuestCompleted(), quest.getTitle()));
                player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getQuestCompletionSound()), jobQuests.getConfigManager().getQuestCompletionSoundVolume(), jobQuests.getConfigManager().getQuestCompletionSoundPitch());
                jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId()).setCompletedDate(LocalDateTime.now());
                long oldLevel = playerJob.getLevel();
                jobQuests.getPlayerManager().giveRewards(player.getUniqueId(), job.getId(), quest.getId());
                long newLevel = jobQuests.getPlayerManager().changeJobLevel(player.getUniqueId(), job.getId());
                if (oldLevel < newLevel) {
                    jobQuests.getMessageUtil().sendMessage(player, MessageFormat.format(jobQuests.getMessageConfig().getJobLevelUp(), job.getName(), oldLevel, newLevel));
                    player.playSound(player.getLocation(), Sound.valueOf(jobQuests.getConfigManager().getJobLevelUpSound()), jobQuests.getConfigManager().getJobLevelUpSoundVolume(), jobQuests.getConfigManager().getJobLevelUpSoundPitch());
                }
            }
        }));
    }

    private int getCraftedAmount(CraftItemEvent craftItemEvent) {
        int craftedAmount = craftItemEvent.getRecipe().getResult().getAmount();
        if (craftItemEvent.getClick().isShiftClick()) {
            final int baseAmount = craftedAmount;
            int leastAmount = -1;
            for (ItemStack item : craftItemEvent.getInventory().getMatrix()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                final int amount = item.getAmount();
                if (leastAmount == -1 || amount < leastAmount) {
                    leastAmount = amount;
                }
            }
            int possibleAmount = getPossibleAmount(craftItemEvent);
            craftedAmount = Math.min(leastAmount * baseAmount, possibleAmount);
        }
        return craftedAmount;
    }

    private int getPossibleAmount(CraftItemEvent craftItemEvent) {
        int possibleAmount = 0;
        final int itemMaxStackSize = craftItemEvent.getRecipe().getResult().getMaxStackSize();
        for (ItemStack item : craftItemEvent.getWhoClicked().getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                possibleAmount += itemMaxStackSize;
            }
            if (item != null && item.isSimilar(craftItemEvent.getRecipe().getResult())) {
                possibleAmount += itemMaxStackSize - item.getAmount();
            }
        }
        final int baseAmount = craftItemEvent.getRecipe().getResult().getAmount();
        final int remain = possibleAmount % baseAmount;
        if (remain > 0) {
            final int numberOfBase = possibleAmount / baseAmount;
            possibleAmount = (numberOfBase + 1) * baseAmount;
        }
        return possibleAmount;
    }
}
