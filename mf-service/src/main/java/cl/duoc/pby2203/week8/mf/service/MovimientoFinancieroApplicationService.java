package cl.duoc.pby2203.week8.mf.service;

import cl.duoc.pby2203.week8.events.DomainEvent;
import cl.duoc.pby2203.week8.events.TopicNames;
import cl.duoc.pby2203.week8.events.MovimientoFinancieroApprovedEvent;
import cl.duoc.pby2203.week8.events.MovimientoFinancieroCreatedEvent;
import cl.duoc.pby2203.week8.events.MovimientoFinancieroRejectedEvent;
import cl.duoc.pby2203.week8.mf.api.CreateMovimientoFinancieroRequest;
import cl.duoc.pby2203.week8.mf.domain.MovimientoFinanciero;
import cl.duoc.pby2203.week8.mf.repository.MovimientoFinancieroRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MovimientoFinancieroApplicationService {

    private static final Logger log =
            LoggerFactory.getLogger(MovimientoFinancieroApplicationService.class);

    private final MovimientoFinancieroRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MovimientoFinancieroApplicationService(
            MovimientoFinancieroRepository repository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Crea un nuevo movimientoFinanciero.
     *
     * movimientoFinanciero queda inicialmente en PENDING
     * y se publica el evento MovimientoFinancieroCreated.
     */
    public MovimientoFinanciero create(CreateMovimientoFinancieroRequest request) {

        MovimientoFinanciero movimientoFinanciero = repository.save(
                new MovimientoFinanciero(request.fecha(), request.monto(), request.tipo())
        );

        String movimientoFinancieroId = movimientoFinanciero.getMovimientoFinancieroId();
        UUID eventId = movimientoFinanciero.getMovimientoFinancieroCreatedEventId();

        MovimientoFinancieroCreatedEvent event = new MovimientoFinancieroCreatedEvent(
                eventId,
                movimientoFinancieroId,
                Instant.now(),
                request.fecha(),
                request.monto(),
                request.tipo()
        );

        publish(TopicNames.MOVIMIENTO_FINANCIERO_CREATED, event);

        log.info(
                "[PRODUCER] MovimientoFinancieroCreated publicado movimientoFinancieroId={} eventId={}",
                movimientoFinancieroId,
                eventId
        );

        return movimientoFinanciero;
        }

    /**
     * Solicita la aprobación de un movimientoFinanciero.
     *
     * Este método NO cambia directamente el estado.
     * Publica un MovimientoFinancieroApprovedEvent.
     *
     * El MovimientoFinancieroSagaListener será quien procese
     * el evento y cambie el estado a CONFIRMED.
     */
    public void approve(String movimientoFinancieroId) {

        MovimientoFinanciero movimientoFinanciero = find(movimientoFinancieroId);

        UUID eventId = UUID.randomUUID();

        MovimientoFinancieroApprovedEvent event = new MovimientoFinancieroApprovedEvent(
                eventId,
                movimientoFinanciero.getMovimientoFinancieroId(),
                Instant.now()
        );

        publish(TopicNames.MOVIMIENTO_FINANCIERO_APPROVED, event);

        log.info(
                "[PRODUCER] MovimientoFinancieroApproved publicado movimientoFinancieroId={} eventId={}",
                movimientoFinancieroId,
                eventId
        );
    }

    /**
     * Solicita el rechazo de un movimientoFinanciero.
     *
     * Este método NO cambia directamente el estado.
     * Publica un MovimientoFinancieroRejectedEvent.
     *
     * El MovimientoFinancieroSagaListener será quien procese
     * el evento y realice la compensación.
     */
    public void reject(String movimientoFinancieroId, String reason) {

        MovimientoFinanciero movimientoFinanciero = find(movimientoFinancieroId);

        UUID eventId = UUID.randomUUID();

        MovimientoFinancieroRejectedEvent event = new MovimientoFinancieroRejectedEvent(
                eventId,
                movimientoFinanciero.getMovimientoFinancieroId(),
                Instant.now(),
                reason
        );

        publish(TopicNames.MOVIMIENTO_FINANCIERO_REJECTED, event);

        log.info(
                "[PRODUCER] MovimientoFinancieroRejected publicado movimientoFinancieroId={} eventId={} reason={}",
                movimientoFinancieroId,
                eventId,
                reason
        );
    }

    /**
     * Republica el evento MovimientoFinancieroCreated original.
     *
     * El eventId original se mantiene deliberadamente para
     * demostrar el comportamiento frente a eventos duplicados.
     */
    public void replayCreated(String movimientoFinancieroId) {

        MovimientoFinanciero movimientoFinanciero = repository.findById(movimientoFinancieroId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "MovimientoFinanciero not found: " + movimientoFinancieroId
                        )
                );

        MovimientoFinancieroCreatedEvent event = new MovimientoFinancieroCreatedEvent(
                movimientoFinanciero.getMovimientoFinancieroCreatedEventId(),
                movimientoFinanciero.getMovimientoFinancieroId(),
                movimientoFinanciero.getCreatedAt(),
                movimientoFinanciero.getFecha(),
                movimientoFinanciero.getMonto(),
                movimientoFinanciero.getTipo()
        );

        publish(TopicNames.MOVIMIENTO_FINANCIERO_CREATED, event);

        log.warn(
                "[DEMO DUPLICADO] MovimientoFinancieroCreated republicado con el MISMO eventId={}",
                event.eventId()
        );
    }

    /**
     * Busca un movimientoFinanciero existente.
     */
    private MovimientoFinanciero find(String movimientoFinancieroId) {

        return repository.findById(movimientoFinancieroId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "MovimientoFinanciero not found: " + movimientoFinancieroId
                        )
                );
    }

    /**
     * Publica un evento de dominio en Kafka.
     */
    private void publish(String topic, DomainEvent event) {

        kafkaTemplate.send(
                topic,
                event.movimientoFinancieroId(),
                event
        );
    }
}