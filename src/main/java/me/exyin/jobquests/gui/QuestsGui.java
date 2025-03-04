package me.exyin.jobquests.gui;

import lombok.Getter;
import lombok.Setter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.gui.interfaces.PageableGui;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.Quest;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.player.PlayerObjective;
import me.exyin.jobquests.model.player.PlayerQuest;
import me.exyin.jobquests.model.rewards.RewardFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestsGui implements InventoryHolder, PageableGui {
    private final JobQuests jobQuests;
    private final JobsForQuestsGui jobsForQuestsGui;
    private final Inventory inventory;
    private final UUID playerUuid;
    private final String jobId;
    private final Job job;
    @Getter
    private int backButtonSlot;
    @Getter
    private int prevPageButtonSlot;
    @Getter
    private int nextPageButtonSlot;
    @Getter
    @Setter
    private int pIndex;
    private final Map<Integer, Map<Integer, ItemStack>> pages;

    public QuestsGui(JobQuests jobQuests, JobsForQuestsGui jobsForQuestsGui, UUID playerUuid, String jobId) {
        this.jobQuests = jobQuests;
        this.jobsForQuestsGui = jobsForQuestsGui;
        this.playerUuid = playerUuid;
        this.jobId = jobId;
        this.job = jobQuests.getJobManager().getJob(jobId);
        this.inventory = jobQuests.getServer().createInventory(this, 9 * jobQuests.getGuiConfig().getQuestGuiRows(), jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiTitle(), job.getName())));
        this.pIndex = 1;
        this.pages = new HashMap<>();
        setupItems();
        setupPage(pIndex);
    }

    public void setupItems() {
        backButtonSlot = jobQuests.getGuiConfig().getQuestGuiBackButtonSlot() + 9 * (jobQuests.getGuiConfig().getQuestGuiRows() - 1);
        AtomicInteger slot = new AtomicInteger(0);
        AtomicInteger pageIndex = new AtomicInteger(1);
        Map<Integer, ItemStack> page = new HashMap<>();
        PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(playerUuid, jobId);
        job.getQuests().forEach(quest -> {
            if (slot.get() >= inventory.getSize() - 9) {
                pages.put(pageIndex.get(), new HashMap<>(page));
                page.clear();
                slot.set(0);
                pageIndex.incrementAndGet();
            }
            if (playerJob.getPlayerQuests().stream().filter(playerQuest -> playerQuest.getQuestId() == quest.getId()).toList().isEmpty()) {
                return;
            }
            PlayerQuest playerQuest = jobQuests.getPlayerManager().getPlayerQuest(playerUuid, jobId, quest.getId());
            ItemStack itemStack;
            if (quest.getRequiredLevel() > playerJob.getLevel()) {
                itemStack = getLockedQuestItem(jobId, quest);
            } else if (playerQuest.getCompletedDate() != null) {
                itemStack = getCompletedQuestItem(jobId, quest, playerQuest);
            } else {
                itemStack = getQuestItem(jobId, quest);
            }
            page.put(slot.get(), itemStack);
            slot.incrementAndGet();
        });
        pages.put(pageIndex.get(), new HashMap<>(page));
    }

    @Override
    public void setupPage(int pageIndex) {
        prevPageButtonSlot = -1;
        nextPageButtonSlot = -1;
        ItemStack emptySlot = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getQuestGuiEmpty(), "", new ArrayList<>(), 1, false, -1, null);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, emptySlot);
        }
        ItemStack backButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getQuestGuiBackButtonMaterial(), jobQuests.getGuiConfig().getQuestGuiBackButtonName(), jobQuests.getGuiConfig().getQuestGuiBackButtonLore().stream().map(line -> jobQuests.getMessageUtil().toMiniMessageComponent(line)).toList(), 1, jobQuests.getGuiConfig().isQuestGuiBackButtonEnchanted(), jobQuests.getGuiConfig().getQuestGuiBackButtonCustomModelData(), null);
        inventory.setItem(backButtonSlot, backButton);
        Map<Integer, ItemStack> page = pages.get(pageIndex);
        for (Map.Entry<Integer, ItemStack> entry : page.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        if (pages.size() <= 1) {
            return;
        }
        if (pageIndex > 1) {
            List<Component> modifiedLore = jobQuests.getGuiConfig().getQuestGuiPrevPageButtonLore().stream().map(line -> jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(line, pageIndex - 1))).toList();
            ItemStack prevPageButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getQuestGuiPrevPageButtonMaterial(), jobQuests.getGuiConfig().getQuestGuiPrevPageButtonName(), modifiedLore, 1, jobQuests.getGuiConfig().isQuestGuiPrevPageButtonEnchanted(), jobQuests.getGuiConfig().getQuestGuiPrevPageButtonCustomModelData(), null);
            prevPageButtonSlot = jobQuests.getGuiConfig().getQuestGuiPrevPageButtonSlot() + 9 * (jobQuests.getGuiConfig().getQuestGuiRows() - 1);
            inventory.setItem(prevPageButtonSlot, prevPageButton);
        }
        if (pageIndex < pages.size()) {
            List<Component> modifiedLore = jobQuests.getGuiConfig().getQuestGuiNextPageButtonLore().stream().map(line -> jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(line, pageIndex + 1))).toList();
            ItemStack nextPageButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getQuestGuiNextPageButtonMaterial(), jobQuests.getGuiConfig().getQuestGuiNextPageButtonName(), modifiedLore, 1, jobQuests.getGuiConfig().isQuestGuiNextPageButtonEnchanted(), jobQuests.getGuiConfig().getQuestGuiNextPageButtonCustomModelData(), null);
            nextPageButtonSlot = jobQuests.getGuiConfig().getQuestGuiNextPageButtonSlot() + 9 * (jobQuests.getGuiConfig().getQuestGuiRows() - 1);
            inventory.setItem(nextPageButtonSlot, nextPageButton);
        }
    }

    private ItemStack getQuestItem(String jobId, Quest quest) {
        Material material = jobQuests.getGuiConfig().getQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<Component> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line;
            if (playerObjective.getProgression() >= objective.getQuantity()) {
                line = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemCompletedObjective(), "<objective>");
            } else {
                line = MessageFormat.format(jobQuests.getGuiConfig().getQuestItemObjective(), "<objective>");
            }
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("objective", objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(line, placeholders));
        });
        quest.getRewards().forEach(reward -> {
            RewardFactory rewardFactory = new RewardFactory(jobQuests);
            lore.add(rewardFactory.getStrategy(reward.getType()).getDescription(reward.getQuantity()));
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(now, quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getQuestItemRefreshTime(), refreshTime)));
        return jobQuests.getGuiUtil().getItemStack(material, name, lore, 1, jobQuests.getGuiConfig().isQuestItemEnchanted(), jobQuests.getGuiConfig().getQuestItemCustomModelData(), null);
    }

    private ItemStack getLockedQuestItem(String jobId, Quest quest) {
        Material material = jobQuests.getGuiConfig().getLockedQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<Component> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line = MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemObjective(), "<objective>");
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("objective", objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(line, placeholders));
        });
        quest.getRewards().forEach(reward -> {
            RewardFactory rewardFactory = new RewardFactory(jobQuests);
            lore.add(rewardFactory.getStrategy(reward.getType()).getDescription(reward.getQuantity()));
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(now, quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getLockedQuestItemRefreshTime(), refreshTime)));
        return jobQuests.getGuiUtil().getItemStack(material, name, lore, 1, jobQuests.getGuiConfig().isLockedQuestItemEnchanted(), jobQuests.getGuiConfig().getLockedQuestItemCustomModelData(), null);
    }

    private ItemStack getCompletedQuestItem(String jobId, Quest quest, PlayerQuest playerQuest) {
        Material material = jobQuests.getGuiConfig().getCompletedQuestItemMaterial();
        String name = MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemName(), quest.getTitle(), quest.getRequiredLevel());
        List<Component> lore = new ArrayList<>();
        quest.getObjectives().forEach(objective -> {
            PlayerObjective playerObjective = jobQuests.getPlayerManager().getPlayerObjective(playerUuid, jobId, quest.getId(), objective.getId());
            String line = MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemObjective(), "<objective>");
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("objective", objective.getObjectiveType().getDescription(playerObjective.getProgression(), objective.getQuantity()));
            lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(line, placeholders));
        });
        quest.getRewards().forEach(reward -> {
            RewardFactory rewardFactory = new RewardFactory(jobQuests);
            lore.add(rewardFactory.getStrategy(reward.getType()).getDescription(reward.getQuantity()));
        });
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshDate = jobQuests.getTimeUtil().getRefreshDate(playerQuest.getCompletedDate(), quest.getRefreshTime());
        String refreshTime = jobQuests.getTimeUtil().getTimeFormattedFromDates(now, refreshDate);
        lore.add(jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getCompletedQuestItemRefreshTime(), refreshTime)));
        return jobQuests.getGuiUtil().getItemStack(material, name, lore, 1, jobQuests.getGuiConfig().isCompletedQuestItemEnchanted(), jobQuests.getGuiConfig().getCompletedQuestItemCustomModelData(), null);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Inventory getBackInventory() {
        return jobsForQuestsGui.getInventory();
    }
}
