package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.entity.EntityType;

import java.text.MessageFormat;

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
    public String getDescription(int progression, int quantity) {
        return MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.KILL), progression, quantity, type.translationKey());
    }

    @Override
    public String getCompletedMessage(int quantity) {
        return MessageFormat.format(jobQuests.getMessageConfig().getObjectiveKILLCompleted(), quantity, type.translationKey());
    }
}
