package me.exyin.jobQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class Quest {
    private int id;
    private String title;
    private int requiredLevel;
    private List<Objective> objectives;
    private List<Reward> rewards;
}
