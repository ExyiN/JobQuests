package me.exyin.jobquests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerQuest {
    private int questId;
    private LocalDateTime completedDate;
    private List<PlayerObjective> playerObjectives;
}
