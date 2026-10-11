package cl.translog.bff.web.dto;

public record EstadoFinancieroDto(

    Long id,
    Integer cuentaId,
    String fecha,
    String transaccion,
    Double monto,
    String descripcion
){} 