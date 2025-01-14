package me.exyin.jobQuests.model.objectives.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exyin.jobQuests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.entity.EntityType;

@Getter
@ToString
@NoArgsConstructor
public class ObjectiveTypeKillStrategy implements ObjectiveType {
    private EntityType type;

    @Override
    public void setType(String type) {
        this.type = EntityType.valueOf(type);
    }
}
