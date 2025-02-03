package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@ToString
public class ObjectiveTypeKillStrategy implements ObjectiveType {
    @Getter
    private EntityType type;
    private final JobQuests jobQuests;

    public ObjectiveTypeKillStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = EntityType.valueOf(type);
    }

    @Override
    public Component getDescription(int progression, int quantity) {
        String message = MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.KILL), progression, quantity, "<type>");
        Map<String, Component> placeholders = new HashMap<>();
        placeholders.put("type", Component.translatable(type.translationKey()));
        return jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders);
    }

    @Override
    public Component getCompletedMessage(int quantity) {
        String message = MessageFormat.format(jobQuests.getMessageConfig().getObjectiveKILLCompleted(), quantity, "<type>");
        Map<String, Component> placeholders = new HashMap<>();
        placeholders.put("type", Component.translatable(type.translationKey()));
        return jobQuests.getMessageUtil().toMiniMessageComponent(message, placeholders);
    }
}
