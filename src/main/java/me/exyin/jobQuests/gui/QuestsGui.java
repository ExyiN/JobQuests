package me.exyin.jobQuests.gui;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.model.Quest;
import me.exyin.jobQuests.model.player.PlayerJob;
import me.exyin.jobQuests.model.player.PlayerObjective;
import me.exyin.jobQuests.model.player.PlayerQuest;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestsGui implements InventoryHolder {
    private final JobQuests jobQuests;
    private final Inventory inventory;
    private final UUID playerUuid;
    private final String jobId;
    private final Job job;

    public QuestsGui(JobQuests jobQuests, UUID playerUuid, String jobId) {
        this.jobQuests = jobQuests;
        this.playerUuid = playerUuid;
        this.jobId = jobId;
        job = jobQuests.getJobManager().getJob(jobId);
        inventory = jobQuests.getServer().createInventory(this, 9 * jobQuests.getGuiConfig().getQuestGuiRows(), jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiTitle(), job.getName())));
        setupItems();
    }

    public void setupItems() {
        ItemStack emptySlot = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getQuestGuiEmpty(), "", new ArrayList<>(), 1, false);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, emptySlot);
        }
        AtomicInteger slot = new AtomicInteger();
        PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(playerUuid, jobId);
        job.getQuests().forEach(quest -> {
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(playerUuid, jobId, quest.getId());
            ItemStack itemStack;
            if (quest.getRequiredLevel() > playerJob.getLevel()) {
                itemStack = getLockedQuestItem(jobId, quest);
            } else if (playerQuest.getCompletedDate() != null) {
                itemStack = getCompletedQuestItem(jobId, quest, playerQuest);
            } else {
                itemStack = getQuestItem(jobId, quest);
            }
            inventory.setItem(slot.get(), itemStack);
            slot.getAndIncrement();
        });
    }

    private ItemStack getQuestItem(String jobId, Quest quest) {
        Material material = jobQuests.getGuiConfig().getQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<String> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line;
            if (playerObjective.getProgression() >= objective.getQuantity()) {
                line = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemCompletedObjective(), objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            } else {
                line = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemObjective(), objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            }
            lore.add(line);
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(now, quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(MessageFormat.format(jobQuests.getGuiConfig().getQuestItemRefreshTime(), refreshTime));
        List<String> modifiedLore = lore.stream().map(line -> "<!i><white>" + line).toList();
        return jobQuests.getGuiUtil().getItemStack(material, name, modifiedLore, 1, jobQuests.getGuiConfig().isQuestItemEnchanted());
    }

    private ItemStack getLockedQuestItem(String jobId, Quest quest) {
        Material material = jobQuests.getGuiConfig().getLockedQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<String> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line = MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemObjective(), objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            lore.add(line);
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(now, quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemRefreshTime(), refreshTime));
        List<String> modifiedLore = lore.stream().map(line -> "<!i><white>" + line).toList();
        return jobQuests.getGuiUtil().getItemStack(material, name, modifiedLore, 1, jobQuests.getGuiConfig().isLockedQuestItemEnchanted());
    }

    private ItemStack getCompletedQuestItem(String jobId, Quest quest, PlayerQuest playerQuest) {
        Material material = jobQuests.getGuiConfig().getCompletedQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<String> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line = MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemObjective(), objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            lore.add(line);
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(playerQuest.getCompletedDate(), quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemRefreshTime(), refreshTime));
        List<String> modifiedLore = lore.stream().map(line -> "<!i><white>" + line).toList();
        return jobQuests.getGuiUtil().getItemStack(material, name, modifiedLore, 1, jobQuests.getGuiConfig().isCompletedQuestItemEnchanted());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
