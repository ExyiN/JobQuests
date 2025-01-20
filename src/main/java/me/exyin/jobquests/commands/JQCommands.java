package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.gui.JQGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JQCommands implements CommandExecutor, TabCompleter {
    private final JobQuests jobQuests;
    private final List<String> availableCommands = List.of("purgejobs", "resetjob", "resetquest", "reload");

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
        if (!commandSender.hasPermission("jobquests.admin")) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getNoPerm());
            return true;
        }
        JQCommandFactory jqCommandFactory = new JQCommandFactory(jobQuests, availableCommands);
        jqCommandFactory.getStrategy(args[0]).execute(commandSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("jobquests.use")
                || !commandSender.hasPermission("jobquests.admin")) {
            return List.of();
        }
        if (args.length == 1) {
            return availableCommands;
        }
        JQCommandFactory jqCommandFactory = new JQCommandFactory(jobQuests, availableCommands);
        return jqCommandFactory.getStrategy(args[0]).getTabCompletion(args);
    }
}
