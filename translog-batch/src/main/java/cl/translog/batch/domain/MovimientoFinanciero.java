package cl.translog.batch.domain;

public class MovimientoFinanciero {
    private Integer id;
    private String fecha;
    private Double monto;
    private String tipo;
    private String estado;

    public MovimientoFinanciero() {}

    public MovimientoFinanciero(Integer id, String fecha, Double monto, String tipo, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


    @Override
    public String toString() {
        return "MovimientoFinanciero{" +
                "id=" + id +
                ", fecha='" + fecha + '\'' +
                ", monto='" + monto + '\'' +
                ", tipo='" + tipo + '\'' +
                ", estado=" + estado + 
                '}';
    }


}