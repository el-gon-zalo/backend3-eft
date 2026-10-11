package cl.duoc.pby2203.week8.events;

import java.time.Instant;
import java.util.UUID;

/** Hecho final fallido de la saga: la reserva fue cancelada. */

public record MovimientoFinancieroCancelledEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt,
        String reason
        ) 
implements DomainEvent {}
