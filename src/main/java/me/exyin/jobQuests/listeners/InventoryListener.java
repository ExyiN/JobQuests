package me.exyin.jobQuests.listeners;

import me.exyin.jobQuests.gui.JQGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof JQGui jqGui)) {
            return;
        }
        event.setCancelled(true);
    }
}
