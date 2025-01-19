package me.exyin.jobquests.model.objectives;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeBreakStrategy;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeKillStrategy;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveFactory {
    private final Map<ObjectiveEventType, ObjectiveType> map = new HashMap<>();

    public ObjectiveFactory(JobQuests jobQuests) {
        map.put(ObjectiveEventType.KILL, new ObjectiveTypeKillStrategy(jobQuests));
        map.put(ObjectiveEventType.BREAK, new ObjectiveTypeBreakStrategy(jobQuests));
    }

    public ObjectiveType getStrategy(ObjectiveEventType objectiveEventType) {
        return map.get(objectiveEventType);
    }
}
