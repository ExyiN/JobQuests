package me.exyin.jobquests.model.objectives;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.ObjectiveEventType;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeBreakStrategy;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeCraftStrategy;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeFishStrategy;
import me.exyin.jobquests.model.objectives.impl.ObjectiveTypeKillStrategy;
import me.exyin.jobquests.model.objectives.interfaces.ObjectiveType;

import java.util.EnumMap;

public class ObjectiveFactory {
    private final EnumMap<ObjectiveEventType, ObjectiveType> map = new EnumMap<>(ObjectiveEventType.class);

    public ObjectiveFactory(JobQuests jobQuests) {
        map.put(ObjectiveEventType.KILL, new ObjectiveTypeKillStrategy(jobQuests));
        map.put(ObjectiveEventType.BREAK, new ObjectiveTypeBreakStrategy(jobQuests));
        map.put(ObjectiveEventType.FISH, new ObjectiveTypeFishStrategy(jobQuests));
        map.put(ObjectiveEventType.CRAFT, new ObjectiveTypeCraftStrategy(jobQuests));
    }

    public ObjectiveType getStrategy(ObjectiveEventType objectiveEventType) {
        return map.get(objectiveEventType);
    }
}
