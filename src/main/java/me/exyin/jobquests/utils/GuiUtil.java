package me.exyin.jobquests.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.exyin.jobquests.JobQuests;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class GuiUtil {
    private final JobQuests jobQuests;

    public GuiUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    public ItemStack getItemStack(Material material, String name, List<Component> lore, int amount, boolean isEnchanted, int customModelData, UUID uuid) {
        ItemStack item = new ItemStack(material, amount);
        item.editMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                PlayerProfile playerProfile = jobQuests.getServer().createProfile(uuid);
                skullMeta.setPlayerProfile(playerProfile);
            }
            if (name != null) {
                itemMeta.displayName(jobQuests.getMessageUtil().toMiniMessageComponent("<!i>" + name));
                if (name.isBlank()) {
                    itemMeta.setHideTooltip(true);
                }
            }
            if (!lore.isEmpty()) {
                List<Component> modifiedLore = lore.stream().map(line -> jobQuests.getMessageUtil().toMiniMessageComponent("<!i><white>").append(line)).toList();
                itemMeta.lore(modifiedLore);
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
