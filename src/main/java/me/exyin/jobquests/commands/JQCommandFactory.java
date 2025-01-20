package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.impl.*;

import java.util.*;

public class JQCommandFactory {
    private final Map<String, JQCommand> map = new HashMap<>();
    private final List<String> availableCommands;

    public JQCommandFactory(JobQuests jobQuests, List<String> availableCommands) {
        this.availableCommands = availableCommands;
        map.put(availableCommands.getFirst(), new JQCommandPurgeJobsStrategy(jobQuests));
        map.put(availableCommands.get(1), new JQCommandResetJobStrategy(jobQuests));
        map.put(availableCommands.get(2), new JQCommandResetQuestStrategy(jobQuests));
        map.put(availableCommands.get(3), new JQCommandReloadStrategy(jobQuests));
        map.put("default", new JQCommandDefaultStrategy(jobQuests));
    }

    public JQCommand getStrategy(String command) {
        if (!availableCommands.contains(command)) {
            return map.get("default");
        }
        return map.get(command);
    }
}
