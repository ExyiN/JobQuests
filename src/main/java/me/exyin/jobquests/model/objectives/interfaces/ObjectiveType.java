package me.exyin.jobquests.model.objectives.interfaces;

public interface ObjectiveType {
    void setType(String type);
    Object getType();
    String getDescription(int progression, int quantity);
    String getCompletedMessage(int quantity);
}
