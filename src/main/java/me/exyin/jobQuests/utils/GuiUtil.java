package me.exyin.jobQuests.utils;

import me.exyin.jobQuests.JobQuests;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiUtil {
    private final JobQuests jobQuests;

    public GuiUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    public ItemStack getItemStack(Material material, String name, List<String> lore, int amount, boolean isEnchanted) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();
        if (name != null) {
            itemMeta.itemName(jobQuests.getMessageUtil().toMiniMessageComponent(name));
            if (name.isBlank()) {
                itemMeta.setHideTooltip(true);
            }
        }
        if (!lore.isEmpty()) {
            List<String> modifiedLore = lore.stream().map(line -> "<!i><white>" + line).toList();
            itemMeta.lore(modifiedLore.stream().map(line -> jobQuests.getMessageUtil().toMiniMessageComponent(line)).toList());
        }
        itemMeta.setEnchantmentGlintOverride(isEnchanted);
        itemMeta.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier(new NamespacedKey(jobQuests, "hide"), 0, AttributeModifier.Operation.ADD_NUMBER));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
}
