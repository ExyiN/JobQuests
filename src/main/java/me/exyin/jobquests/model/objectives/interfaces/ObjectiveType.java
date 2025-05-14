package me.exyin.jobquests.model.objectives.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;

public interface ObjectiveType {
    void setType(String type);
    Translatable getType();
    Component getDescription(int progression, int quantity);
    Component getCompletedMessage(int quantity);
}
