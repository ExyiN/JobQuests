package me.exyin.jobquests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class LeaderboardPlayer {
    private UUID uuid;
    private long level;
    private double xp;
}
