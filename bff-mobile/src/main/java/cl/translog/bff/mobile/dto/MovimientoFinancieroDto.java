package cl.translog.bff.mobile.dto;

public record MovimientoFinancieroDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 