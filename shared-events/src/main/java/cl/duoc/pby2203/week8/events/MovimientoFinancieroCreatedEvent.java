package cl.duoc.pby2203.week8.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Hecho: una nueva reserva fue creada y quedó pendiente de completar la saga. */
public record MovimientoFinancieroCreatedEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt,
        String fecha, 
        Double monto, 
        String tipo
        ) 
implements DomainEvent {}
