package cl.translog.batch.config;

import cl.translog.batch.domain.Interes;
import cl.translog.batch.exception.TemporaryInteresException;
import cl.translog.batch.listener.JobCompletionListener;
import cl.translog.batch.listener.InteresSkipListener;
import cl.translog.batch.partition.EntityPartitioner;
import cl.translog.batch.policy.InteresSkipPolicy;
import cl.translog.batch.processor.InteresProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.RetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.dao.CannotAcquireLockException;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class InteresBatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<Interes> interesReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        int safeStart = start == null ? 0 : start;
        int safeEnd = end == null ? 0 : end;

        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader-" + safeStart + "-" + safeEnd)
                .resource(new ClassPathResource("input/intereses_trimestrales.csv"))
                .linesToSkip(1)
                .currentItemCount(safeStart)
                .maxItemCount(safeEnd + 1)
                .delimited()
                .names(
                        "cuenta_id",
                        "nombre",
                        "saldo_original",
                        "edad",
                        "tipo"
                )
                .fieldSetMapper(fieldSet -> {

                    Interes interes = new Interes();

                    interes.setCuentaId(
                            fieldSet.readInt("cuenta_id")
                    );

                    interes.setNombre(
                            fieldSet.readString("nombre")
                    );

                    interes.setSaldoOriginal(
                            fieldSet.readDouble("saldo_original")
                    );

                    interes.setEdad(
                            fieldSet.readInt("edad")
                    );

                    interes.setTipo(
                            fieldSet.readString("tipo")
                    );

                    return interes;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Interes> interesWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Interes>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO interes_processed
                        (
                            cuenta_id,
                            nombre,
                            saldo_original,
                            edad,
                            tipo,
                            interes_aplicado,
                            saldo_final
                        )
                        VALUES
                        (
                            :cuentaId,
                            :nombre,
                            :saldoOriginal,
                            :edad,
                            :tipo,
                            :interesAplicado,
                            :saldoFinal
                        )
                        """)
                .beanMapped()
                .build();
    }

       @Bean
        public RetryPolicy interesRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions =
                new HashMap<>();

        exceptions.put(
                TemporaryInteresException.class,
                true
        );

        exceptions.put(
                org.springframework.dao.CannotAcquireLockException.class,
                true
        );

        return new SimpleRetryPolicy(
                6,
                exceptions,
                true
        );
        }

    @Bean
    public BackOffPolicy interesBackOffPolicy() {

        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy policy =
            new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();

        policy.setInitialInterval(500);
        policy.setMultiplier(2);
        policy.setMaxInterval(4000);

        return policy;
    }

    @Bean
    public Step interesWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Interes> interesReader,
            InteresProcessor interesProcessor,
            JdbcBatchItemWriter<Interes> interesWriter,
            InteresSkipPolicy interesSkipPolicy,
            InteresSkipListener interesSkipListener,
            RetryPolicy interesRetryPolicy,
            BackOffPolicy interesBackOffPolicy,
            @Value("${app.chunk-size}") int chunkSize) {

        return new StepBuilder(
                "interesWorkerStep",
                jobRepository
        )
                .<Interes, Interes>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skipPolicy(interesSkipPolicy)
                .retryPolicy(interesRetryPolicy)
                .backOffPolicy(interesBackOffPolicy)
                .listener(interesSkipListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler interesPartitionHandler(
            Step interesWorkerStep,
            TaskExecutor taskExecutor,
            @Value("${app.grid-size}") int gridSize) {

        TaskExecutorPartitionHandler handler =
                new TaskExecutorPartitionHandler();

        handler.setStep(interesWorkerStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);

        return handler;
    }

    @Bean
    public Step interesPartitionStep(
            JobRepository jobRepository,
            EntityPartitioner partitioner,
            TaskExecutorPartitionHandler interesPartitionHandler) {

        return new StepBuilder(
                "interesPartitionStep",
                jobRepository
        )
                .partitioner(
                        "interesWorkerStep",
                        partitioner
                )
                .partitionHandler(
                        interesPartitionHandler
                )
                .build();
    }

        @Bean
        public Step interesReportStep(
                JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                DataSource dataSource,
                @Value("${app.output-file-intereses}") String outputFile) {

        return new StepBuilder(
                "interesReportStep",
                jobRepository
        )
                .tasklet(
                        (contribution, chunkContext) -> {

                                JdbcTemplate jdbc =
                                        new JdbcTemplate(dataSource);

                                List<Map<String, Object>> rows =
                                        jdbc.queryForList("""
                                                SELECT
                                                cuenta_id,
                                                nombre,
                                                saldo_original,
                                                edad,
                                                tipo,
                                                interes_aplicado,
                                                saldo_final
                                                FROM interes_processed
                                                """);

                                Path path =
                                        Paths.get(outputFile);

                                Path parent = path.getParent();

                                if (parent != null) {
                                Files.createDirectories(parent);
                                }

                                try (BufferedWriter writer =
                                        Files.newBufferedWriter(path)) {

                                writer.write(
                                        "cuenta_id,nombre,saldo_original,edad,tipo,interes_aplicado,saldo_final"
                                );
                                writer.newLine();

                                for (Map<String, Object> row : rows) {
                                        writer.write(
                                                row.get("cuenta_id") + ","
                                                        + row.get("nombre") + ","
                                                        + row.get("saldo_original") + ","
                                                        + row.get("edad") + ","
                                                        + row.get("tipo") + ","
                                                        + row.get("interes_aplicado") + ","
                                                        + row.get("saldo_final")
                                        );
                                        writer.newLine();
                                }
                                }

                                System.out.println(
                                        "Reporte generado en: "
                                                + path.toAbsolutePath()
                                );

                                return RepeatStatus.FINISHED;
                        },
                        transactionManager
                )
                .build();
        }

    @Bean
    public Job procesarInteresJob(
            JobRepository jobRepository,
            Step interesPartitionStep,
            Step interesReportStep,
            JobCompletionListener listener) {

        return new JobBuilder(
                "procesarInteresJob",
                jobRepository
        )
                .listener(listener)
                .start(interesPartitionStep)
                .next(interesReportStep)
                .build();
    }
}
