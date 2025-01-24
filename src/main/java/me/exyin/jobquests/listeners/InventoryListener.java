package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.gui.interfaces.JQGui;
import me.exyin.jobquests.gui.LeaderboardGui;
import me.exyin.jobquests.gui.interfaces.PageableGui;
import me.exyin.jobquests.gui.QuestsGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    private final JobQuests jobQuests;

    public InventoryListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof JQGui) && !(inventory.getHolder(false) instanceof QuestsGui) && !(inventory.getHolder(false) instanceof LeaderboardGui)) {
            return;
        }
        event.setCancelled(true);
        inventory = event.getClickedInventory();
        if (inventory != null
                && inventory.getHolder(false) instanceof JQGui jqGui
                && jobQuests.getGuiConfig().getJobGuiSlot().get(event.getSlot()) != null) {
            event.getWhoClicked().openInventory(jqGui.getOnClickInventory(event.getWhoClicked().getUniqueId(), event.getSlot()));
        }
        if (inventory != null
                && inventory.getHolder(false) instanceof PageableGui pageableGui) {
            if (event.getSlot() == pageableGui.getBackButtonSlot()) {
                event.getWhoClicked().openInventory(pageableGui.getBackInventory());
            }
            if (event.getSlot() == pageableGui.getPrevPageButtonSlot()) {
                pageableGui.setPIndex(pageableGui.getPIndex() - 1);
                pageableGui.setupPage(pageableGui.getPIndex());
            }
            if (event.getSlot() == pageableGui.getNextPageButtonSlot()) {
                pageableGui.setPIndex(pageableGui.getPIndex() + 1);
                pageableGui.setupPage(pageableGui.getPIndex());
            }
        }
    }
}
