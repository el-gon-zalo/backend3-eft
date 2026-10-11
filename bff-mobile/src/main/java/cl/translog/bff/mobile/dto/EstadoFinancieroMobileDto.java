package cl.translog.bff.mobile.dto;

public record EstadoFinancieroMobileDto(

    Long id,
    Integer cuentaId,
    String fecha,
    String transaccion
){} 