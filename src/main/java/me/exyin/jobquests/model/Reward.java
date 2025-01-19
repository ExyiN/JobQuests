package me.exyin.jobquests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exyin.jobquests.model.enums.RewardType;

@Getter
@ToString
@AllArgsConstructor
public class Reward {
    private int id;
    private RewardType type;
    private double quantity;
}
