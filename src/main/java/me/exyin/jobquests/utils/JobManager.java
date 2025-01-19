package me.exyin.jobquests.utils;

import lombok.Getter;
import lombok.Setter;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.Quest;

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
}
