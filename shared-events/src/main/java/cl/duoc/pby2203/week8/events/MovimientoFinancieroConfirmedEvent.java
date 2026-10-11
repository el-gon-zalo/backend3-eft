package cl.duoc.pby2203.week8.events;

import java.time.Instant;
import java.util.UUID;

/** Hecho final exitoso de la saga: la reserva quedó confirmada. */
public record MovimientoFinancieroConfirmedEvent(
        UUID eventId, 
        String movimientoFinancieroId, 
        Instant occurredAt,
        String fecha, 
        Double monto, 
        String tipo
        ) 
implements DomainEvent {}
