package me.exyin.jobquests.listeners;

import me.exyin.jobquests.JobQuests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final JobQuests jobQuests;

    public PlayerListener(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        if(jobQuests.getPlayerManager().isPlayerLoaded(playerJoinEvent.getPlayer().getUniqueId())) {
            return;
        }
        jobQuests.getPlayerManager().loadPlayer(playerJoinEvent.getPlayer().getUniqueId());
        jobQuests.getPlayerManager().updatePlayer(playerJoinEvent.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        jobQuests.getPlayerManager().savePlayer(playerQuitEvent.getPlayer().getUniqueId());
        jobQuests.getPlayerManager().unloadPlayer(playerQuitEvent.getPlayer().getUniqueId());
    }
}
