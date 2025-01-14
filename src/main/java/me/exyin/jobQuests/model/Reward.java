package me.exyin.jobQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exyin.jobQuests.model.enums.RewardType;

@Getter
@ToString
@AllArgsConstructor
public class Reward {
    private int id;
    private RewardType type;
    private int quantity;
}
