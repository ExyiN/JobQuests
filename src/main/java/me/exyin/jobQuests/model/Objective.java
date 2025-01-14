package me.exyin.jobQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exyin.jobQuests.model.enums.ObjectiveEventType;
import me.exyin.jobQuests.model.objectives.interfaces.ObjectiveType;

@Getter
@ToString
@AllArgsConstructor
public class Objective {
    private int id;
    private ObjectiveEventType objectiveEventType;
    private ObjectiveType objectiveType;
    private int quantity;
}
