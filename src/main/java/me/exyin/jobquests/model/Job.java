package me.exyin.jobquests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Job {
    private String id;
    private String name;
    private Material material;
    private int customModelData;
    private List<String> description;
    private List<Quest> quests;

}
