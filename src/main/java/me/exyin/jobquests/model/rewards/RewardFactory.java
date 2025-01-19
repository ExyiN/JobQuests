package me.exyin.jobquests.model.rewards;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.RewardType;
import me.exyin.jobquests.model.rewards.impl.RewardMoneyStrategy;
import me.exyin.jobquests.model.rewards.impl.RewardXpStrategy;
import me.exyin.jobquests.model.rewards.interfaces.Reward;

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
