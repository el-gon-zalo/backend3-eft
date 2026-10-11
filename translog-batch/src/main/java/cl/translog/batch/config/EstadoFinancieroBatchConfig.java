package cl.translog.batch.config;

import cl.translog.batch.domain.EstadoFinanciero;
import cl.translog.batch.exception.TemporaryEstadoFinancieroException;
import cl.translog.batch.listener.JobCompletionListener;
import cl.translog.batch.listener.EstadoFinancieroSkipListener;
import cl.translog.batch.partition.EntityPartitioner;
import cl.translog.batch.policy.EstadoFinancieroSkipPolicy;
import cl.translog.batch.processor.EstadoFinancieroProcessor;

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
public class EstadoFinancieroBatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<EstadoFinanciero> estadoFinancieroReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        int safeStart = start == null ? 0 : start;
        int safeEnd = end == null ? 0 : end;

        return new FlatFileItemReaderBuilder<EstadoFinanciero>()
                .name("estadoFinancieroReader-" + safeStart + "-" + safeEnd)
                .resource(new ClassPathResource("input/estados_financieros_anuales.csv"))
                .linesToSkip(1)
                .currentItemCount(safeStart)
                .maxItemCount(safeEnd + 1)
                .delimited()
                .names(
                        "cuenta_id",
                        "fecha",
                        "transaccion",
                        "monto",
                        "descripcion"
                )
                .fieldSetMapper(fieldSet -> {

                    EstadoFinanciero estadoFinanciero = new EstadoFinanciero();

                    estadoFinanciero.setCuentaId(
                            fieldSet.readInt("cuenta_id")
                    );

                    estadoFinanciero.setFecha(
                            fieldSet.readString("fecha")
                    );

                    estadoFinanciero.setTransaccion(
                            fieldSet.readString("transaccion")
                    );

                    estadoFinanciero.setMonto(
                            fieldSet.readDouble("monto")
                    );

                    estadoFinanciero.setDescripcion(
                            fieldSet.readString("descripcion")
                    );

                    return estadoFinanciero;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<EstadoFinanciero> estadoFinancieroWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<EstadoFinanciero>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO estadoFinanciero_processed
                        (
                            cuenta_id,
                            fecha,
                            transaccion,
                            monto,
                            descripcion
                        )
                        VALUES
                        (
                            :cuentaId,
                            :fecha,
                            :transaccion,
                            :monto,
                            :descripcion
                        )
                        """)
                .beanMapped()
                .build();
    }

        @Bean
        public RetryPolicy estadoFinancieroRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions =
                new HashMap<>();

        exceptions.put(
                TemporaryEstadoFinancieroException.class,
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
        public BackOffPolicy estadoFinancieroBackOffPolicy() {

        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy policy =
                new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();

        policy.setInitialInterval(500);
        policy.setMultiplier(2);
        policy.setMaxInterval(4000);

        return policy;
        }

    @Bean
    public Step estadoFinancieroWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<EstadoFinanciero> estadoFinancieroReader,
            EstadoFinancieroProcessor estadoFinancieroProcessor,
            JdbcBatchItemWriter<EstadoFinanciero> estadoFinancieroWriter,
            EstadoFinancieroSkipPolicy estadoFinancieroSkipPolicy,
            EstadoFinancieroSkipListener estadoFinancieroSkipListener,
            RetryPolicy estadoFinancieroRetryPolicy,
            BackOffPolicy estadoFinancieroBackOffPolicy,
            @Value("${app.chunk-size}") int chunkSize) {

        return new StepBuilder(
                "estadoFinancieroWorkerStep",
                jobRepository
        )
                .<EstadoFinanciero, EstadoFinanciero>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(estadoFinancieroReader)
                .processor(estadoFinancieroProcessor)
                .writer(estadoFinancieroWriter)
                .faultTolerant()
                .skipPolicy(estadoFinancieroSkipPolicy)
                .retryPolicy(estadoFinancieroRetryPolicy)
                .backOffPolicy(estadoFinancieroBackOffPolicy)
                .listener(estadoFinancieroSkipListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler estadoFinancieroPartitionHandler(
            Step estadoFinancieroWorkerStep,
            TaskExecutor taskExecutor,
            @Value("${app.grid-size}") int gridSize) {

        TaskExecutorPartitionHandler handler =
                new TaskExecutorPartitionHandler();

        handler.setStep(estadoFinancieroWorkerStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);

        return handler;
    }

    @Bean
    public Step partitionStep(
            JobRepository jobRepository,
            EntityPartitioner partitioner,
            TaskExecutorPartitionHandler estadoFinancieroPartitionHandler) {

        return new StepBuilder(
                "partitionStep",
                jobRepository
        )
                .partitioner(
                        "estadoFinancieroWorkerStep",
                        partitioner
                )
                .partitionHandler(
                        estadoFinancieroPartitionHandler
                )
                .build();
    }

        @Bean
        public Step estadoFinancieroReportStep(
                JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                DataSource dataSource,
                @Value("${app.output-file-estados-financieros}") String outputFile) {

        return new StepBuilder(
                "estadoFinancieroReportStep",
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
                                                fecha,
                                                transaccion,
                                                monto,
                                                descripcion
                                                FROM estadoFinanciero_processed
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
                                        "cuenta_id,fecha,transaccion,monto,descripcion"
                                );
                                writer.newLine();

                                for (Map<String, Object> row : rows) {
                                        writer.write(
                                                row.get("cuenta_id") + ","
                                                        + row.get("fecha") + ","
                                                        + row.get("transaccion") + ","
                                                        + row.get("monto") + ","
                                                        + row.get("descripcion")
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
    public Job procesarEstadoFinancieroJob(
            JobRepository jobRepository,
            Step partitionStep,
            Step estadoFinancieroReportStep,
            JobCompletionListener listener) {

        return new JobBuilder(
                "procesarEstadoFinancieroJob",
                jobRepository
        )
                .listener(listener)
                .start(partitionStep)
                .next(estadoFinancieroReportStep)
                .build();
    }
}
