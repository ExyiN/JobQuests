package me.exyin.jobQuests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerQuest {
    private int questId;
    private Date completedDate;
    private Set<PlayerObjective> playerObjectives;
}
