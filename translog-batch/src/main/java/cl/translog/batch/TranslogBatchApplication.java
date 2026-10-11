package cl.translog.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TranslogBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslogBatchApplication.class, args);
    }

    @Bean
    CommandLineRunner launchJobs(JobLauncher jobLauncher, List<Job> jobs) {
        return args -> {
            for (Job job : jobs) {
                JobExecution execution = jobLauncher.run(
                        job,
                        new JobParametersBuilder()
                                .addLong("time", System.currentTimeMillis())
                                .addString("jobName", job.getName())
                                .toJobParameters()
                );
                System.out.println(
                        "Job '" + job.getName() + "' terminó con estado: " + execution.getStatus()
                );
            }
        };
    }
}