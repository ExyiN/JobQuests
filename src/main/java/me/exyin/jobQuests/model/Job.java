package me.exyin.jobQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Job {
    private String id;
    private String name;
    private Material material;
    private List<String> lore;
    private Set<Quest> quests;
}
