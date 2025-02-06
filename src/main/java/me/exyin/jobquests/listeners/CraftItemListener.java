package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerQuest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class CraftItemListener extends JQListener implements Listener {
    public CraftItemListener(JobQuests jobQuests) {
        super(jobQuests);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent craftItemEvent) {
        Player player = (Player) craftItemEvent.getWhoClicked();
        if (isEventNotTriggered(player, craftItemEvent)) {
            return;
        }
        jobQuests.getJobManager().getJobs().forEach(job -> job.getQuests().forEach(quest -> {
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(player.getUniqueId(), job.getId());
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(player.getUniqueId(), job.getId(), quest.getId());
            if (playerQuest.getCompletedDate() != null || quest.getRequiredLevel() > playerJob.getLevel()) {
                return;
            }
            quest.getObjectives().forEach(objective -> {
                if (skipObjective(job, quest, objective, player.getUniqueId(), ObjectiveEventType.CRAFT, craftItemEvent.getRecipe().getResult().getType())) {
                    return;
                }
                jobQuests.getPlayerManager().incrementProgression(player.getUniqueId(), job.getId(), quest.getId(), objective.getId(), getCraftedAmount(craftItemEvent));
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
        CraftItemEvent craftItemEvent = (CraftItemEvent) event;
        return !player.hasPermission("jobquests.use")
                || jobQuests.getConfigManager().getWorldBlacklist().contains(player.getWorld().getName())
                || jobQuests.getConfigManager().getGameModeBlacklist().contains(player.getGameMode())
                || craftItemEvent.getAction() == InventoryAction.NOTHING
                || getCraftedAmount(craftItemEvent) == 0;
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
