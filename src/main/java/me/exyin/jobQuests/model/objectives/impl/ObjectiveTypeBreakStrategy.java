package me.exyin.jobQuests.model.objectives.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.Material;

import java.text.MessageFormat;

@Getter
@ToString
@NoArgsConstructor
public class ObjectiveTypeBreakStrategy implements ObjectiveType {
    private Material type;
    private JobQuests jobQuests;

    public ObjectiveTypeBreakStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void setType(String type) {
        this.type = Material.valueOf(type);
    }

    @Override
    public String getDescription(int progression, int quantity) {
        return MessageFormat.format(jobQuests.getMessageConfig().getObjectiveBREAKDesc(), progression, quantity, type.toString().toLowerCase());
    }
}
