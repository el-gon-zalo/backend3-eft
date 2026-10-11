package cl.duoc.pby2203.week8.mf.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movimientoFinanciero_processed")
public class MovimientoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fecha;
    private Double monto;
    private String tipo;
    private String estado;

    // No existen en la tabla: solo viven en memoria
    @Transient
    private Instant createdAt = Instant.now();
    @Transient
    private String failureReason;

    protected MovimientoFinanciero() {
        // requerido por JPA
    }

    /** Para movimientos creados desde mf-service (el id lo asigna MySQL). */
    public MovimientoFinanciero(String fecha, Double monto, String tipo) {
        this.fecha = fecha;
        this.monto = monto;
        this.tipo = tipo;
        this.estado = MovimientoFinancieroStatus.PENDING.name();
    }

    /** El id de la tabla (Integer) se expone como String hacia los eventos y la API. */
    public String getMovimientoFinancieroId() {
        return id == null ? null : String.valueOf(id);
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Instant getCreatedAt() { return createdAt; }

    /** Determinístico: el mismo movimiento siempre produce el mismo eventId (necesario para replay). */
    public UUID getMovimientoFinancieroCreatedEventId() {
        if (id == null) return null;
        return UUID.nameUUIDFromBytes(
                ("movimiento-financiero-created-" + id).getBytes(StandardCharsets.UTF_8));
    }

    public MovimientoFinancieroStatus getStatus() {
        try {
            return MovimientoFinancieroStatus.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return MovimientoFinancieroStatus.PENDING; // valores del batch que no son del saga
        }
    }

    public String getFailureReason() { return failureReason; }

    public void confirm() {
        this.estado = MovimientoFinancieroStatus.CONFIRMED.name();
        this.failureReason = null;
    }

    public void cancel(String reason) {
        this.estado = MovimientoFinancieroStatus.CANCELLED.name();
        this.failureReason = reason;
    }
}