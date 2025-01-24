package me.exyin.jobquests.gui;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.player.PlayerJob;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobsForLeaderboardGui implements InventoryHolder, JQGui {
    private final JobQuests jobQuests;
    private final Inventory inventory;
    private final UUID playerUuid;

    public JobsForLeaderboardGui(JobQuests jobQuests, UUID playerUuid) {
        this.jobQuests = jobQuests;
        this.inventory = jobQuests.getServer().createInventory(this, 9 * jobQuests.getGuiConfig().getJobGuiRows(), jobQuests.getMessageUtil().toMiniMessageComponent(jobQuests.getGuiConfig().getJobGuiTitle()));
        this.playerUuid = playerUuid;
        setupItems();
    }

    public void setupItems() {
        ItemStack emptySlot = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getJobGuiEmpty(), "", new ArrayList<>(), 1, false, -1, null);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, emptySlot);
        }
        jobQuests.getGuiConfig().getJobGuiSlot().forEach((slot, jobId) -> {
            Job job = jobQuests.getJobManager().getJob(jobId);
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(playerUuid, jobId);
            long level = playerJob.getLevel();
            String itemName = MessageFormat.format(jobQuests.getGuiConfig().getJobItemName(), job.getName(), level);
            double nextLevelRequiredXp = jobQuests.getPlayerManager().getNextLevelRequiredXp(level);
            List<String> lore = new ArrayList<>(job.getDescription());
            jobQuests.getGuiConfig().getJobItemLore().forEach(line -> lore.add(MessageFormat.format(line, String.format("%.2f", playerJob.getXp()), String.format("%.2f", nextLevelRequiredXp))));
            ItemStack itemStack = jobQuests.getGuiUtil().getItemStack(job.getMaterial(), itemName, lore, 1, jobQuests.getGuiConfig().isJobItemEnchanted(), job.getCustomModelData(), null);
            inventory.setItem(slot, itemStack);
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Inventory getOnClickInventory(UUID uuid, int slot) {
        LeaderboardGui leaderboardGui = new LeaderboardGui(jobQuests, this, jobQuests.getGuiConfig().getJobGuiSlot().get(slot));
        return leaderboardGui.getInventory();
    }
}
