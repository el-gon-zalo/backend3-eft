package cl.duoc.pby2203.week8.events;

/**
 * Nombres de tópicos centralizados para evitar strings repetidos en los servicios.
 *
 * <p>Los tópicos describen hechos del dominio. Mantener los nombres en un único lugar
 * facilita leer el código y evita errores de escritura entre productores y consumidores.</p>
 */
public final class TopicNames {
    private TopicNames() {}

    public static final String MOVIMIENTO_FINANCIERO_CREATED = "movimiento-financiero.created";
    public static final String MOVIMIENTO_FINANCIERO_PENDING = "movimiento-financiero.pending";
    public static final String MOVIMIENTO_FINANCIERO_CONFIRMED = "movimiento-financiero.confirmed";
    public static final String MOVIMIENTO_FINANCIERO_CANCELLED = "movimiento-financiero.cancelled";
    public static final String MOVIMIENTO_FINANCIERO_APPROVED = "movimiento-financiero.approved"; 
    public static final String MOVIMIENTO_FINANCIERO_REJECTED = "movimiento-financiero.rejected"; 
    public static final String MOVIMIENTO_FINANCIERO_REVERSAL_REQUESTED = "movimiento-financiero.reversal-requested"; 
}