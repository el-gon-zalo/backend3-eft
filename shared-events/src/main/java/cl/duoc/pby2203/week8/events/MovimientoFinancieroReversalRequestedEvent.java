package cl.duoc.pby2203.week8.events;

import java.util.UUID;
import java.time.Instant;

public record MovimientoFinancieroReversalRequestedEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt,
        Double monto, 
        String reason
        ) 
implements DomainEvent {}