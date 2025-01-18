package me.exyin.jobQuests.gui;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.model.player.PlayerJob;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JQGui implements InventoryHolder {
    private final JobQuests jobQuests;
    private final Inventory inventory;
    private final UUID playerUuid;

    public JQGui(JobQuests jobQuests, UUID playerUuid) {
        this.jobQuests = jobQuests;
        this.inventory = jobQuests.getServer().createInventory(this, 9 * jobQuests.getGuiConfig().getJobRows());
        this.playerUuid = playerUuid;
        setupItems();
    }

    public void setupItems() {
        ItemStack emptySlot = jobQuests.getGuiUtil().getItemStack(jobQuests.getGuiConfig().getJobEmpty(), "", new ArrayList<>(), 1);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, emptySlot);
        }
        jobQuests.getGuiConfig().getJobSlot().forEach((jobId, slot) -> {
            Job job = jobQuests.getJobManager().getJob(jobId);
            PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(playerUuid, jobId);
            long level = playerJob.getLevel();
            String itemName = MessageFormat.format(jobQuests.getGuiConfig().getJobName(), job.getName(), level);
            double nextLevelRequiredXp = jobQuests.getPlayerManager().getNextLevelRequiredXp(level);
            List<String> lore = new ArrayList<>(job.getDescription());
            jobQuests.getGuiConfig().getJobLore().forEach(line -> lore.add(MessageFormat.format(line, String.format("%.2f", playerJob.getXp()), String.format("%.2f", nextLevelRequiredXp))));
            List<String> modifiedLore = lore.stream().map(line -> "<!i><white>" + line).toList();
            ItemStack itemStack = jobQuests.getGuiUtil().getItemStack(job.getMaterial(), itemName, modifiedLore, 1);
            inventory.setItem(slot, itemStack);
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
