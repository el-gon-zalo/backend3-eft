package cl.duoc.pby2203.week8.mf.config;

import cl.duoc.pby2203.week8.events.TopicNames;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicsConfiguration {

    private NewTopic topic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean NewTopic movimientoFinancieroCreated() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_CREATED); }
    @Bean NewTopic movimientoFinancieroPending() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_PENDING); }
    @Bean NewTopic movimientoFinancieroConfirmed() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_CONFIRMED); }
    @Bean NewTopic movimientoFinancieroCancelled() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_CANCELLED); }
    @Bean NewTopic movimientoFinancieroApproved() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_APPROVED); }
    @Bean NewTopic movimientoFinancieroRejected() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_REJECTED); }
    @Bean NewTopic movimientoFinancieroReversalRequested() { return topic(TopicNames.MOVIMIENTO_FINANCIERO_REVERSAL_REQUESTED); }
}
