package me.exyin.jobQuests.model.rewards;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.enums.RewardType;
import me.exyin.jobQuests.model.rewards.impl.RewardMoneyStrategy;
import me.exyin.jobQuests.model.rewards.impl.RewardXpStrategy;
import me.exyin.jobQuests.model.rewards.interfaces.Reward;

import java.util.HashMap;
import java.util.Map;

public class RewardFactory {
    private final Map<RewardType, Reward> map = new HashMap<>();

    public RewardFactory(JobQuests jobQuests) {
        map.put(RewardType.XP, new RewardXpStrategy(jobQuests));
        map.put(RewardType.MONEY, new RewardMoneyStrategy(jobQuests));
    }

    public Reward getStrategy(RewardType rewardType) {
        return map.get(rewardType);
    }
}
