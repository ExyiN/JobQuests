package me.exyin.jobquests.commands;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.enums.JQCommandsEnum;
import me.exyin.jobquests.commands.impl.*;
import me.exyin.jobquests.commands.interfaces.JQCommand;

import java.util.EnumMap;

public class JQCommandFactory {
    private final EnumMap<JQCommandsEnum, JQCommand> map = new EnumMap<>(JQCommandsEnum.class);

    public JQCommandFactory(JobQuests jobQuests) {
        map.put(JQCommandsEnum.HELP, new JQCommandDefaultStrategy(jobQuests));
        map.put(JQCommandsEnum.PURGEJOBS, new JQCommandPurgeJobsStrategy(jobQuests));
        map.put(JQCommandsEnum.RELOAD, new JQCommandReloadStrategy(jobQuests));
        map.put(JQCommandsEnum.RESETJOB, new JQCommandResetJobStrategy(jobQuests));
        map.put(JQCommandsEnum.RESETQUEST, new JQCommandResetQuestStrategy(jobQuests));
        map.put(JQCommandsEnum.SETLEVEL, new JQCommandSetLevelStrategy(jobQuests));
    }

    public JQCommand getStrategy(JQCommandsEnum command) {
        return map.get(command);
    }
}
