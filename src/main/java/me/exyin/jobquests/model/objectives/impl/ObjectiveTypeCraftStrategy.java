package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ObjectiveTypeCraftStrategy implements ObjectiveType {
    @Getter
    private Material type;
    private final JobQuests jobQuests;

    public ObjectiveTypeCraftStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = Material.valueOf(type);
    }

    @Override
    public Component getDescription(int progression, int quantity) {
        String message = MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.CRAFT), progression, quantity, "<type>");
        Map<String, Component> placeholders = new HashMap<>();
        placeholders.put("type", Component.translatable(type.translationKey()));
        return jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders);
    }

    @Override
    public Component getCompletedMessage(int quantity) {
        String message = MessageFormat.format(jobQuests.getMessageConfig().getObjectiveCRAFTCompleted(), quantity, "<type>");
        Map<String, Component> placeholders = new HashMap<>();
        placeholders.put("type", Component.translatable(type.translationKey()));
        return jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders);
    }
}
