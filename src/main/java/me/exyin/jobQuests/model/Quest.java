package me.exyin.jobQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
public class Quest {
    private int id;
    private String title;
    private int requiredLevel;
    private Set<Objective> objectives;
    private Set<Reward> rewards;
}
