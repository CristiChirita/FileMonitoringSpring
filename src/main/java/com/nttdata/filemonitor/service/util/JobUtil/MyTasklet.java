package com.nttdata.filemonitor.service.util.JobUtil;


import com.nttdata.filemonitor.service.JobService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import com.nttdata.filemonitor.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

public class MyTasklet implements Tasklet{

    @Autowired
    JobService jobService;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        //System.out.println("Hello This is a sample example of spring batch");
        Job entity = new Job().value("It works!").date(ZonedDateTime.now());
        jobService.save(entity);
        return RepeatStatus.FINISHED;
    }
}
