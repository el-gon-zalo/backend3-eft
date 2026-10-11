package cl.translog.batch.config;

import cl.translog.batch.domain.MovimientoFinanciero;
import cl.translog.batch.exception.TemporaryMovimientoFinancieroException;
import cl.translog.batch.listener.JobCompletionListener;
import cl.translog.batch.listener.MovimientoFinancieroSkipListener;
import cl.translog.batch.partition.EntityPartitioner;
import cl.translog.batch.policy.MovimientoFinancieroSkipPolicy;
import cl.translog.batch.processor.MovimientoFinancieroProcessor;
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
public class MovimientoFinancieroBatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<MovimientoFinanciero> movimientoFinancieroReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        int safeStart = start == null ? 0 : start;
        int safeEnd = end == null ? 0 : end;

        return new FlatFileItemReaderBuilder<MovimientoFinanciero>()
                .name("movimientoFinancieroReader-" + safeStart + "-" + safeEnd)
                .resource(new ClassPathResource("input/movimientos_financieros_diarios.csv"))
                .linesToSkip(1)
                .currentItemCount(safeStart)
                .maxItemCount(safeEnd + 1)
                .delimited()
                .names(
                        "id",
                        "fecha",
                        "monto",
                        "tipo"
                )
                .fieldSetMapper(fieldSet -> {

                    MovimientoFinanciero movimientoFinanciero = new MovimientoFinanciero();

                    movimientoFinanciero.setId(
                            fieldSet.readInt("id")
                    );

                    movimientoFinanciero.setFecha(
                            fieldSet.readString("fecha")
                    );

                    movimientoFinanciero.setMonto(
                            fieldSet.readDouble("monto")
                    );

                    movimientoFinanciero.setTipo(
                            fieldSet.readString("tipo")
                    );

                    return movimientoFinanciero;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<MovimientoFinanciero> movimientoFinancieroWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<MovimientoFinanciero>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO movimientoFinanciero_processed
                        (
                        id,
                        fecha,
                        monto,
                        tipo,
                        estado
                        )
                        VALUES
                        (
                        :id,
                        :fecha,
                        :monto,
                        :tipo,
                        :estado
                        )
                        ON DUPLICATE KEY UPDATE
                        fecha = VALUES(fecha),
                        monto = VALUES(monto),
                        tipo = VALUES(tipo),
                        estado = VALUES(estado)
                        """)
                .beanMapped()
                .build();
    }

        @Bean
        public RetryPolicy movimientoFinancieroRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions =
                new HashMap<>();

        exceptions.put(
                TemporaryMovimientoFinancieroException.class,
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
    public BackOffPolicy movimientoFinancieroBackOffPolicy() {

        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy policy =
            new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();

        policy.setInitialInterval(500);
        policy.setMultiplier(2);
        policy.setMaxInterval(4000);

        return policy;
    }

    @Bean
    public Step movimientoFinancieroWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<MovimientoFinanciero> movimientoFinancieroReader,
            MovimientoFinancieroProcessor movimientoFinancieroProcessor,
            JdbcBatchItemWriter<MovimientoFinanciero> movimientoFinancieroWriter,
            MovimientoFinancieroSkipPolicy movimientoFinancieroSkipPolicy,
            MovimientoFinancieroSkipListener movimientoFinancieroSkipListener,
            RetryPolicy movimientoFinancieroRetryPolicy,
            BackOffPolicy movimientoFinancieroBackOffPolicy,
            @Value("${app.chunk-size}") int chunkSize) {

        return new StepBuilder(
                "movimientoFinancieroWorkerStep",
                jobRepository
        )
                .<MovimientoFinanciero, MovimientoFinanciero>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(movimientoFinancieroReader)
                .processor(movimientoFinancieroProcessor)
                .writer(movimientoFinancieroWriter)
                .faultTolerant()
                .skipPolicy(movimientoFinancieroSkipPolicy)
                .retryPolicy(movimientoFinancieroRetryPolicy)
                .backOffPolicy(movimientoFinancieroBackOffPolicy)
                .listener(movimientoFinancieroSkipListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler movimientoFinancieroPartitionHandler(
            Step movimientoFinancieroWorkerStep,
            TaskExecutor taskExecutor,
            @Value("${app.grid-size}") int gridSize) {

        TaskExecutorPartitionHandler handler =
                new TaskExecutorPartitionHandler();

        handler.setStep(movimientoFinancieroWorkerStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);

        return handler;
    }

    @Bean
    public Step movimientoFinancieroPartitionStep(
            JobRepository jobRepository,
            EntityPartitioner partitioner,
            TaskExecutorPartitionHandler movimientoFinancieroPartitionHandler) {

        return new StepBuilder(
                "movimientoFinancieroPartitionStep",
                jobRepository
        )
                .partitioner(
                        "movimientoFinancieroWorkerStep",
                        partitioner
                )
                .partitionHandler(
                        movimientoFinancieroPartitionHandler
                )
                .build();
    }

        @Bean
        public Step movimientoFinancieroReportStep(
                JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                DataSource dataSource,
                @Value("${app.output-file-movimientos-financieros}") String outputFile) {

        return new StepBuilder(
                "movimientoFinancieroReportStep",
                jobRepository
        )
                .tasklet(
                        (contribution, chunkContext) -> {

                                JdbcTemplate jdbc =
                                        new JdbcTemplate(dataSource);

                                List<Map<String, Object>> rows =
                                        jdbc.queryForList("""
                                                SELECT id, fecha, monto, tipo, estado
                                                FROM movimientoFinanciero_processed
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
                                        "id,fecha,monto,tipo,estado"
                                );
                                writer.newLine();

                                for (Map<String, Object> row : rows) {
                                        writer.write(
                                                row.get("id") + ","
                                                        + row.get("fecha") + ","
                                                        + row.get("monto") + ","
                                                        + row.get("tipo") + ","
                                                        + row.get("estado")
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
    public Job procesarMovimientoFinancieroesJob(
            JobRepository jobRepository,
            Step movimientoFinancieroPartitionStep,
            Step movimientoFinancieroReportStep,
            JobCompletionListener listener) {

        return new JobBuilder(
                "procesarMovimientoFinancieroesJob",
                jobRepository
        )
                .listener(listener)
                .start(movimientoFinancieroPartitionStep)
                .next(movimientoFinancieroReportStep)
                .build();
    }
}
