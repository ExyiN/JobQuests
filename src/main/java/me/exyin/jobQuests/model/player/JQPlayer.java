package me.exyin.jobQuests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class JQPlayer {
    private UUID uuid;
    private List<PlayerJob> playerJobs;
}
