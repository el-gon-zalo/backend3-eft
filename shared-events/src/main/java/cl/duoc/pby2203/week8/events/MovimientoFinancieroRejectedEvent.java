package cl.duoc.pby2203.week8.events;

import java.util.UUID;
import java.time.Instant;

public record MovimientoFinancieroRejectedEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt,
        String reason
        ) 
implements DomainEvent {}