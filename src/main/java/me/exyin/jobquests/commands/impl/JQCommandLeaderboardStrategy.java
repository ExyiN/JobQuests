package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import me.exyin.jobquests.gui.JobsForLeaderboardGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class JQCommandLeaderboardStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandLeaderboardStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean canExecute(CommandSender commandSender) {
        return commandSender instanceof Player && commandSender.hasPermission("jobquests.use");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        JobsForLeaderboardGui jobsForLeaderboardGui = new JobsForLeaderboardGui(jobQuests, player.getUniqueId());
        player.openInventory(jobsForLeaderboardGui.getInventory());
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }
}
