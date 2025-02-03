package me.exyin.jobquests.model.objectives.interfaces;

import net.kyori.adventure.text.Component;

public interface ObjectiveType {
    void setType(String type);
    Object getType();
    Component getDescription(int progression, int quantity);
    Component getCompletedMessage(int quantity);
}
