package me.exyin.jobQuests.listeners;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.gui.JQGui;
import me.exyin.jobQuests.gui.QuestsGui;
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
        if (!(inventory.getHolder(false) instanceof JQGui) && !(inventory.getHolder(false) instanceof QuestsGui)) {
            return;
        }
        event.setCancelled(true);
        inventory = event.getClickedInventory();
        if (inventory != null
                && inventory.getHolder(false) instanceof JQGui jqGui
                && jobQuests.getGuiConfig().getJobGuiSlot().get(event.getSlot()) != null) {
            QuestsGui questsGui = new QuestsGui(jobQuests, jqGui, event.getWhoClicked().getUniqueId(), jobQuests.getGuiConfig().getJobGuiSlot().get(event.getSlot()));
            event.getWhoClicked().openInventory(questsGui.getInventory());
        }
        if (inventory != null
                && inventory.getHolder(false) instanceof QuestsGui questsGui) {
            if (event.getSlot() == questsGui.getBackButtonSlot()) {
                event.getWhoClicked().openInventory(questsGui.getJqGui().getInventory());
            }
        }
    }
}
