package cl.duoc.pby2203.week8.events;

import java.util.UUID;
import java.time.Instant;

public record MovimientoFinancieroApprovedEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt
        ) 
implements DomainEvent {}
