package me.exyin.jobQuests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class JQPlayer {
    private UUID uuid;
    private Set<PlayerJob> playerJobs;
}
