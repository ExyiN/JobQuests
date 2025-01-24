package me.exyin.jobquests.gui;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public interface JQGui {
    Inventory getOnClickInventory(UUID uuid, int slot);
}
