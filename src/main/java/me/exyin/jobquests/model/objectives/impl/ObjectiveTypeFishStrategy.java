package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.Material;

import java.text.MessageFormat;

@ToString
public class ObjectiveTypeFishStrategy implements ObjectiveType {
    @Getter
    private Material type;
    private final JobQuests jobQuests;

    public ObjectiveTypeFishStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = Material.valueOf(type);
    }

    @Override
    public String getDescription(int progression, int quantity) {
        return MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.FISH), progression, quantity, type.translationKey());
    }

    @Override
    public String getCompletedMessage(int quantity) {
        return MessageFormat.format(jobQuests.getMessageConfig().getObjectiveFISHCompleted(), quantity, type.translationKey());
    }
}
