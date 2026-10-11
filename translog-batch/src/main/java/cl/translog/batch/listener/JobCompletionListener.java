package cl.translog.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener
        implements JobExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(
                "========== INICIO JOB {} ==========",
                jobExecution.getJobInstance().getJobName()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info(
                "========== FIN JOB status={} ==========",
                jobExecution.getStatus()
        );

        for (StepExecution step : jobExecution.getStepExecutions()) {

            log.info(
                    "Step={} status={} read={} write={} processSkip={} readSkip={} writeSkip={}",
                    step.getStepName(),
                    step.getStatus(),
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getProcessSkipCount(),
                    step.getReadSkipCount(),
                    step.getWriteSkipCount()
            );
        }

        log.info(
                "Inicio={} Fin={}",
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );
    }
}
