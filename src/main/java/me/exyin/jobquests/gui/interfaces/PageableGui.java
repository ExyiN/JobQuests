package me.exyin.jobquests.gui.interfaces;

import org.bukkit.inventory.Inventory;

public interface PageableGui {
    int getBackButtonSlot();
    int getPrevPageButtonSlot();
    int getNextPageButtonSlot();
    int getPIndex();
    void setPIndex(int pIndex);
    void setupPage(int pageIndex);
    Inventory getBackInventory();
}
