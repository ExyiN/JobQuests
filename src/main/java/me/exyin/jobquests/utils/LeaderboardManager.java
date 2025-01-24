package me.exyin.jobquests.utils;

import lombok.Getter;
import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.player.LeaderboardPlayer;

import java.util.*;

public class LeaderboardManager {
    private final JobQuests jobQuests;
    @Getter
    private final Map<String, List<LeaderboardPlayer>> leaderboard;

    public LeaderboardManager(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        leaderboard = new HashMap<>();
    }

    public void updateLeaderboard() {
        leaderboard.clear();
        jobQuests.getPlayerManager().loadAllOfflinePlayers();
        jobQuests.getJobManager().getJobs().forEach(job -> leaderboard.put(job.getId(), new ArrayList<>()));
        jobQuests.getPlayerManager().getJqPlayers().forEach(jqPlayer -> jqPlayer.getPlayerJobs().forEach(playerJob -> {
            if (leaderboard.get(playerJob.getJobId()) == null) {
                return;
            }
            leaderboard.get(playerJob.getJobId()).add(new LeaderboardPlayer(jqPlayer.getUuid(), playerJob.getLevel(), playerJob.getXp()));
        }));
        leaderboard.values().forEach(leaderboardPlayers -> {
            Comparator<LeaderboardPlayer> comparator = Comparator.comparingLong(LeaderboardPlayer::getLevel).thenComparingDouble(LeaderboardPlayer::getXp);
            leaderboardPlayers.sort(comparator.reversed());
        });
        jobQuests.getPlayerManager().unloadAllOfflinePlayers();
    }
}
