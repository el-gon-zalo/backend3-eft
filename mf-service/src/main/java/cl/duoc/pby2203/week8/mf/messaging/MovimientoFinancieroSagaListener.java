package cl.duoc.pby2203.week8.mf.messaging;

import cl.duoc.pby2203.week8.events.*;
import cl.duoc.pby2203.week8.mf.domain.MovimientoFinanciero;
import cl.duoc.pby2203.week8.mf.repository.MovimientoFinancieroRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Participación de movimientoFinanciero-service en una Saga por COREOGRAFÍA.
 *
 * <p>No hay orquestador central: el servicio reacciona a hechos publicados por otros
 * servicios, cambia su estado y emite nuevos eventos.</p>
 */
@Component
public class MovimientoFinancieroSagaListener {
    private static final Logger log = LoggerFactory.getLogger(MovimientoFinancieroSagaListener.class);

    private final MovimientoFinancieroRepository repository;
    private final ProcessedEventStore processedEvents;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MovimientoFinancieroSagaListener(MovimientoFinancieroRepository repository,
                                   ProcessedEventStore processedEvents,
                                   KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.processedEvents = processedEvents;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = TopicNames.MOVIMIENTO_FINANCIERO_APPROVED, groupId = "movimiento-financiero-service-group")
    public void onMovimientoFinancieroApproved(MovimientoFinancieroApprovedEvent event) {
        if (!processedEvents.isFirstTime(event.eventId())) {
            log.warn("[IDEMPOTENCIA] MovimientoFinancieroApproved duplicado ignorado eventId={}", event.eventId());
            return;
        }

        MovimientoFinanciero movimientoFinanciero = find(event.movimientoFinancieroId());
        movimientoFinanciero.confirm();
        repository.save(movimientoFinanciero);

        MovimientoFinancieroConfirmedEvent confirmed = new MovimientoFinancieroConfirmedEvent(
                UUID.randomUUID(), movimientoFinanciero.getMovimientoFinancieroId(), Instant.now(),
                movimientoFinanciero.getFecha(), movimientoFinanciero.getMonto(), movimientoFinanciero.getTipo()
        );
        publish(TopicNames.MOVIMIENTO_FINANCIERO_CONFIRMED, confirmed);
        log.info("[SAGA] MovimientoFinanciero CONFIRMADO id={}", movimientoFinanciero.getMovimientoFinancieroId());
    }

    @KafkaListener(topics = TopicNames.MOVIMIENTO_FINANCIERO_REJECTED, groupId = "movimiento-financiero-service-group")
    public void onMovimientoFinancieroRejected(MovimientoFinancieroRejectedEvent event) {
        if (!processedEvents.isFirstTime(event.eventId())) {
            log.warn("[IDEMPOTENCIA] MovimientoFinancieroRejected duplicado ignorado eventId={}", event.eventId());
            return;
        }

        MovimientoFinanciero movimientoFinanciero = find(event.movimientoFinancieroId());
        movimientoFinanciero.cancel(event.reason());
        repository.save(movimientoFinanciero);
        publishCancellation(movimientoFinanciero, event.reason());

        /*
         * COMPENSACIÓN DE SAGA:
         * El efecto previo de la movimientoFinanciero (por ejemplo, el monto retenido) debe
         * deshacerse, por eso se solicita un reverso.
         */
        MovimientoFinancieroReversalRequestedEvent reversal = new MovimientoFinancieroReversalRequestedEvent(
                UUID.randomUUID(), movimientoFinanciero.getMovimientoFinancieroId(), Instant.now(),
                movimientoFinanciero.getMonto(), "Transaction rejected: " + event.reason()
        );
        publish(TopicNames.MOVIMIENTO_FINANCIERO_REVERSAL_REQUESTED, reversal);
        log.info("[SAGA-COMPENSACION] Solicitado reverso de movimientoFinanciero id={}", movimientoFinanciero.getMovimientoFinancieroId());
    }

    private void publishCancellation(MovimientoFinanciero movimientoFinanciero, String reason) {
        MovimientoFinancieroCancelledEvent cancelled = new MovimientoFinancieroCancelledEvent(
                UUID.randomUUID(), movimientoFinanciero.getMovimientoFinancieroId(), Instant.now(), reason
        );
        publish(TopicNames.MOVIMIENTO_FINANCIERO_CANCELLED, cancelled);
        log.info("[SAGA] MovimientoFinanciero CANCELADO id={} motivo={}", movimientoFinanciero.getMovimientoFinancieroId(), reason);
    }

    private MovimientoFinanciero find(String movimientoFinancieroId) {
        return repository.findById(movimientoFinancieroId)
                .orElseThrow(() -> new IllegalStateException("MovimientoFinanciero not found: " + movimientoFinancieroId));
    }

    private void publish(String topic, DomainEvent event) {
        kafkaTemplate.send(topic, event.movimientoFinancieroId(), event);
    }
}