package me.exyin.jobQuests.model.objectives.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exyin.jobQuests.model.objectives.interfaces.ObjectiveType;
import org.bukkit.Material;

@Getter
@ToString
@NoArgsConstructor
public class ObjectiveTypeBreakStrategy implements ObjectiveType {
    private Material type;

    @Override
    public void setType(String type) {
        this.type = Material.valueOf(type);
    }
}
