package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.gui.JQGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JQCommands implements CommandExecutor, TabCompleter {
    private final JobQuests jobQuests;

    public JQCommands(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                return true;
            }
            JQGui jqGui = new JQGui(jobQuests, player.getUniqueId());
            player.openInventory(jqGui.getInventory());
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!commandSender.hasPermission("jobquests.admin")) {
                    break;
                }
                jobQuests.reloadPlugin();
                jobQuests.getServer().getOnlinePlayers().forEach(player -> jobQuests.getPlayerManager().updatePlayer(player.getUniqueId()));
                jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getReload());
                break;
            case "purgejobs":
                if (args.length < 2) {
                    jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getSpecifyPlayer());
                    break;
                }
                OfflinePlayer playerToPurge = jobQuests.getServer().getOfflinePlayer(args[1]);
                if (!playerToPurge.hasPlayedBefore()) {
                    jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerNotFound());
                    break;
                }
                List<String> jobsRemoved = jobQuests.getPlayerManager().purgePlayerJobs(playerToPurge.getUniqueId());
                jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getPurgePlayer(), playerToPurge.getName(), jobsRemoved.toString()));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> tabCompleteList = new ArrayList<>();
        if (!commandSender.hasPermission("jobquests.use")
                || !commandSender.hasPermission("jobquests.admin")) {
            return tabCompleteList;
        }
        if (args.length == 1) {
            tabCompleteList.add("purgejobs");
            tabCompleteList.add("reload");
        }
        if (args.length == 2) {
            if (args[0].equals("purgejobs")) {
                tabCompleteList.addAll(jobQuests.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            }
        }
        return tabCompleteList;
    }
}
