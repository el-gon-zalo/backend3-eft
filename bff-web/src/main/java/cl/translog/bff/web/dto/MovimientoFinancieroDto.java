package cl.translog.bff.web.dto;

public record MovimientoFinancieroDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 