package me.exyin.jobquests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;

@Getter
@ToString
@AllArgsConstructor
public class Objective {
    private int id;
    private ObjectiveEventType objectiveEventType;
    private ObjectiveType objectiveType;
    private int quantity;
}
