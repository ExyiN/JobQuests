package me.exyin.jobQuests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerJob {
    private String jobId;
    private long level;
    private double xp;
    private List<PlayerQuest> playerQuests;
}
