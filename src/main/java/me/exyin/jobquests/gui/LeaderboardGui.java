package me.exyin.jobquests.gui;

import lombok.Getter;
import lombok.Setter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.player.LeaderboardPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderboardGui implements InventoryHolder, PageableGui {
    private final JobQuests jobQuests;
    private final JobsForLeaderboardGui jobsForLeaderboardGui;
    private final Inventory inventory;
    private final String jobId;
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

    public LeaderboardGui(JobQuests jobQuests, JobsForLeaderboardGui jobsForLeaderboardGui, String jobId) {
        this.jobQuests = jobQuests;
        this.jobsForLeaderboardGui = jobsForLeaderboardGui;
        this.jobId = jobId;
        Job job = jobQuests.getJobManager().getJob(jobId);
        this.inventory = jobQuests.getServer().createInventory(this, 9 * jobQuests.getGuiConfig().getLeaderboardGuiRows(), jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getLeaderboardGuiTitle(), job.getName())));
        this.pIndex = 1;
        this.pages = new HashMap<>();
        setupItems();
        setupPage(pIndex);
    }

    public void setupItems() {
        backButtonSlot = jobQuests.getGuiConfig().getLeaderboardGuiBackButtonSlot() + 9 * (jobQuests.getGuiConfig().getLeaderboardGuiRows() - 1);
        AtomicInteger slot = new AtomicInteger(0);
        AtomicInteger pageIndex = new AtomicInteger(1);
        Map<Integer, ItemStack> page = new HashMap<>();
        List<LeaderboardPlayer> leaderboardPlayers = jobQuests.getLeaderboardManager().getLeaderboard().get(jobId);
        leaderboardPlayers.forEach(leaderboardPlayer -> {
            if (slot.get() >= inventory.getSize() - 9) {
                pages.put(pageIndex.get(), new HashMap<>(page));
                page.clear();
                slot.set(0);
                pageIndex.incrementAndGet();
            }
            String rank = String.valueOf(leaderboardPlayers.indexOf(leaderboardPlayer) + 1);
            String name = jobQuests.getGuiConfig().getLeaderboardGuiRank().get(rank) != null ? jobQuests.getGuiConfig().getLeaderboardGuiRank().get(rank) : jobQuests.getGuiConfig().getLeaderboardGuiRank().get("default");
            name = MessageFormat.format(name, jobQuests.getServer().getOfflinePlayer(leaderboardPlayer.getUuid()).getName(), rank);
            List<String> lore = new ArrayList<>();
            lore.add(MessageFormat.format(jobQuests.getGuiConfig().getLeaderboardGuiJobLevel(), leaderboardPlayer.getLevel()));
            lore.add(MessageFormat.format(jobQuests.getGuiConfig().getLeaderboardGuiJobXp(), String.format("%.2f", leaderboardPlayer.getXp())));
            ItemStack itemStack = jobQuests.getGuiUtil().getItemStack(Material.PLAYER_HEAD, name, lore, 1, false, -1, leaderboardPlayer.getUuid());
            page.put(slot.get(), itemStack);
            slot.incrementAndGet();
        });
        pages.put(pageIndex.get(), new HashMap<>(page));
    }

    public void setupPage(int pageIndex) {
        prevPageButtonSlot = -1;
        nextPageButtonSlot = -1;
        ItemStack emptySlot = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getLeaderboardGuiEmpty(), "", new ArrayList<>(), 1, false, -1, null);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, emptySlot);
        }
        ItemStack backButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getLeaderboardGuiBackButtonMaterial(), jobQuests.getGuiConfig().getLeaderboardGuiBackButtonName(), jobQuests.getGuiConfig().getLeaderboardGuiBackButtonLore(), 1, jobQuests.getGuiConfig().isLeaderboardGuiBackButtonEnchanted(), jobQuests.getGuiConfig().getLeaderboardGuiBackButtonCustomModelData(), null);
        inventory.setItem(backButtonSlot, backButton);
        Map<Integer, ItemStack> page = pages.get(pageIndex);
        for (Map.Entry<Integer, ItemStack> entry : page.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        if (pages.size() <= 1) {
            return;
        }
        if (pageIndex > 1) {
            List<String> modifiedLore = jobQuests.getGuiConfig().getLeaderboardGuiPrevPageButtonLore().stream().map(line -> MessageFormat.format(line, pageIndex - 1)).toList();
            ItemStack prevPageButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getLeaderboardGuiPrevPageButtonMaterial(), jobQuests.getGuiConfig().getLeaderboardGuiPrevPageButtonName(), modifiedLore, 1, jobQuests.getGuiConfig().isLeaderboardGuiPrevPageButtonEnchanted(), jobQuests.getGuiConfig().getLeaderboardGuiPrevPageButtonCustomModelData(), null);
            prevPageButtonSlot = jobQuests.getGuiConfig().getLeaderboardGuiPrevPageButtonSlot() + 9 * (jobQuests.getGuiConfig().getLeaderboardGuiRows() - 1);
            inventory.setItem(prevPageButtonSlot, prevPageButton);
        }
        if (pageIndex < pages.size()) {
            List<String> modifiedLore = jobQuests.getGuiConfig().getLeaderboardGuiNextPageButtonLore().stream().map(line -> MessageFormat.format(line, pageIndex + 1)).toList();
            ItemStack nextPageButton = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getLeaderboardGuiNextPageButtonMaterial(), jobQuests.getGuiConfig().getLeaderboardGuiNextPageButtonName(), modifiedLore, 1, jobQuests.getGuiConfig().isLeaderboardGuiNextPageButtonEnchanted(), jobQuests.getGuiConfig().getLeaderboardGuiNextPageButtonCustomModelData(), null);
            nextPageButtonSlot = jobQuests.getGuiConfig().getLeaderboardGuiNextPageButtonSlot() + 9 * (jobQuests.getGuiConfig().getLeaderboardGuiRows() - 1);
            inventory.setItem(nextPageButtonSlot, nextPageButton);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Inventory getBackInventory() {
        return jobsForLeaderboardGui.getInventory();
    }
}
