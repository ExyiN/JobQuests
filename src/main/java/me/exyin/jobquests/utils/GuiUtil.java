package me.exyin.jobquests.utils;

import me.exyin.jobquests.JobQuests;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiUtil {
    private final JobQuests jobQuests;

    public GuiUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    public ItemStack getItemStack(Material material, String name, List<String> lore, int amount, boolean isEnchanted, int customModelData) {
        ItemStack item = new ItemStack(material, amount);
        item.editMeta(itemMeta -> {
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
            if (customModelData >= 0) {
                itemMeta.setCustomModelData(customModelData);
            }
            itemMeta.setEnchantmentGlintOverride(isEnchanted);
            itemMeta.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier(new NamespacedKey(jobQuests, "hide"), 0, AttributeModifier.Operation.ADD_NUMBER));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        return item;
    }
}
