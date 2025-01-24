package me.exyin.jobquests.gui.interfaces;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public interface JQGui {
    Inventory getOnClickInventory(UUID uuid, int slot);
}
