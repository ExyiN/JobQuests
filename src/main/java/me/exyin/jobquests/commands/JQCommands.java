package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.enums.JQCommandsEnum;
import me.exyin.jobquests.gui.JobsForQuestsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
            JobsForQuestsGui jobsForQuestsGui = new JobsForQuestsGui(jobQuests, player.getUniqueId());
            player.openInventory(jobsForQuestsGui.getInventory());
            return true;
        }
        JQCommandFactory jqCommandFactory = new JQCommandFactory(jobQuests);
        JQCommandsEnum commandsEnum;
        try {
            commandsEnum = JQCommandsEnum.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            commandsEnum = JQCommandsEnum.HELP;
        }
        if (!jqCommandFactory.getStrategy(commandsEnum).canExecute(commandSender)) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getNoPerm());
            return true;
        }
        jqCommandFactory.getStrategy(commandsEnum).execute(commandSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        JQCommandFactory jqCommandFactory = new JQCommandFactory(jobQuests);
        if (args.length == 1) {
            return Arrays.stream(JQCommandsEnum.values()).filter(value -> jqCommandFactory.getStrategy(value).canExecute(commandSender)).map(value -> value.toString().toLowerCase()).filter(suggestion -> suggestion.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        JQCommandsEnum commandsEnum;
        try {
            commandsEnum = JQCommandsEnum.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            commandsEnum = JQCommandsEnum.HELP;
        }
        if (!jqCommandFactory.getStrategy(commandsEnum).canExecute(commandSender)) {
            return List.of();
        }
        return jqCommandFactory.getStrategy(commandsEnum).getTabCompletion(args);
    }
}
