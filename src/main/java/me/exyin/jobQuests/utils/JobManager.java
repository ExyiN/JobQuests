package me.exyin.jobQuests.utils;

import lombok.Getter;
import lombok.Setter;
import me.exyin.jobQuests.model.Job;
import me.exyin.jobQuests.model.Objective;
import me.exyin.jobQuests.model.Quest;

import java.util.List;

@Setter
@Getter
public class JobManager {
    private List<Job> jobs;

    public JobManager(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Job getJob(String jobId) {
        return jobs.stream().filter(job -> job.getId().equals(jobId)).toList().getFirst();
    }

    public Quest getQuest(String jobId, int questId) {
        return getJob(jobId).getQuests().stream().filter(quest -> quest.getId() == questId).toList().getFirst();
    }

    public Objective getObjective(String jobId, int questId, int objectiveId) {
        return getQuest(jobId, questId).getObjectives().stream().filter(objective -> objective.getId() == objectiveId).toList().getFirst();
    }
}
