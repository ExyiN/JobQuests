package me.exyin.jobQuests.utils;

import me.exyin.jobQuests.JobQuests;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiUtil {
    private final JobQuests jobQuests;

    public GuiUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    public ItemStack getItemStack(Material material, String name, List<String> lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();
        if (!name.isBlank()) {
            itemMeta.itemName(jobQuests.getMessageManager().toMiniMessageComponent(name));
        }
        if (!lore.isEmpty()) {
            itemMeta.lore(lore.stream().map(line -> jobQuests.getMessageManager().toMiniMessageComponent(line)).toList());
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
