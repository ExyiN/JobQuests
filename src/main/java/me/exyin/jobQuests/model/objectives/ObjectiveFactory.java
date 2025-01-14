package me.exyin.jobQuests.model.objectives;

import me.exyin.jobQuests.model.enums.ObjectiveEventType;
import me.exyin.jobQuests.model.objectives.impl.ObjectiveTypeBreakStrategy;
import me.exyin.jobQuests.model.objectives.impl.ObjectiveTypeKillStrategy;
import me.exyin.jobQuests.model.objectives.interfaces.ObjectiveType;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveFactory {
    private final Map<ObjectiveEventType, ObjectiveType> map = new HashMap<>();

    public ObjectiveFactory() {
        map.put(ObjectiveEventType.KILL, new ObjectiveTypeKillStrategy());
        map.put(ObjectiveEventType.BREAK, new ObjectiveTypeBreakStrategy());
    }

    public ObjectiveType getStrategy(ObjectiveEventType objectiveEventType) {
        return map.get(objectiveEventType);
    }
}
