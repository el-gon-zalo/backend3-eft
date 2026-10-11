package cl.translog.batch.domain;

public class Interes {

    private Long id;
    private Integer cuentaId;
    private String nombre;
    private Double saldoOriginal;
    private Integer edad;
    private String tipo;
    private Double interesAplicado;
    private Double saldoFinal;

    public Interes() {}

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getSaldoOriginal() { return saldoOriginal; }
    public void setSaldoOriginal(Double saldoOriginal) { this.saldoOriginal = saldoOriginal; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getInteresAplicado() { return interesAplicado; }
    public void setInteresAplicado(Double interesAplicado) { this.interesAplicado = interesAplicado; }

    public Double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(Double saldoFinal) { this.saldoFinal = saldoFinal; }

    @Override
    public String toString() {
        return "Interes{" +
                "id=" + id +
                ", cuenta_id=" + cuentaId +
                ", nombre='" + nombre + '\'' +
                ", saldo_original='" + saldoOriginal + '\'' +
                ", edad='" + edad + '\'' +
                ", tipo=" + tipo +
                ", interes_aplicado=" + interesAplicado +
                ", saldo_final=" + saldoFinal +
                '}';
    }
}