package me.exyin.jobQuests.model.objectives.interfaces;

public interface ObjectiveType {
    void setType(String type);
    Object getType();
    String getDescription(int progression, int quantity);
}
