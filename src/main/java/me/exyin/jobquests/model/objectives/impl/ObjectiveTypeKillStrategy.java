package me.exyin.jobquests.model.objectives.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.entity.EntityType;

import java.text.MessageFormat;

@Getter
@ToString
@NoArgsConstructor
public class ObjectiveTypeKillStrategy implements ObjectiveType {
    private EntityType type;
    private JobQuests jobQuests;

    public ObjectiveTypeKillStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = EntityType.valueOf(type);
    }

    @Override
    public String getDescription(int progression, int quantity) {
        return MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiObjective().get(ObjectiveEventType.KILL), progression, quantity, type.toString().toLowerCase());
    }

    @Override
    public String getCompletedMessage(int quantity) {
        return MessageFormat.format(jobQuests.getMessageConfig().getObjectiveKILLCompleted(), quantity, type.toString().toLowerCase());
    }
}
