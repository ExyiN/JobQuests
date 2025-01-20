package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.gui.JQGui;
import me.exyin.jobquests.model.Job;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JQCommands implements CommandExecutor, TabCompleter {
    private final JobQuests jobQuests;
    private final List<String> availableCommands = Arrays.asList("purgejobs", "resetjob", "resetquest", "reload");

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
        List<String> tabCompleteList = new ArrayList<>();
        if (!commandSender.hasPermission("jobquests.use")
                || !commandSender.hasPermission("jobquests.admin")) {
            return tabCompleteList;
        }
        if (args.length == 1) {
            tabCompleteList.addAll(availableCommands);
        }
        if (args.length == 2 && (args[0].equals(availableCommands.getFirst()) || args[0].equals(availableCommands.get(1)) || args[0].equals(availableCommands.get(2)))) {
            tabCompleteList.addAll(jobQuests.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
        }

        if (args.length == 3 && (args[0].equals(availableCommands.get(1)) || args[0].equals(availableCommands.get(2)))) {
            tabCompleteList.addAll(jobQuests.getJobManager().getJobs().stream().map(Job::getId).toList());
        }

        if (args.length == 4 && args[0].equals(availableCommands.get(2))) {
            String jobId = args[2];
            if (jobQuests.getJobManager().existsJob(jobId)) {
                tabCompleteList.addAll(jobQuests.getJobManager().getJob(jobId).getQuests().stream().map(quest -> String.valueOf(quest.getId())).toList());
            }
        }
        return tabCompleteList;
    }
}
