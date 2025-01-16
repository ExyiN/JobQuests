package me.exyin.jobQuests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerQuest {
    private int questId;
    private LocalDateTime completedDate;
    private Set<PlayerObjective> playerObjectives;
}
