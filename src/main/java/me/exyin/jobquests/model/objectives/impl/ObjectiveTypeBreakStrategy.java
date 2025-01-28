package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.Material;

import java.text.MessageFormat;

@ToString
public class ObjectiveTypeBreakStrategy implements ObjectiveType {
    @Getter
    private Material type;
    private final JobQuests jobQuests;

    public ObjectiveTypeBreakStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = Material.valueOf(type);
    }

    @Override
    public String getDescription(int progression, int quantity) {
        return MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.BREAK), progression, quantity, type.translationKey());
    }

    @Override
    public String getCompletedMessage(int quantity) {
        return MessageFormat.format(jobQuests.getMessageConfig().getObjectiveBREAKCompleted(), quantity, type.translationKey());
    }
}
