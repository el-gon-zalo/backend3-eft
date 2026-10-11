package cl.translog.bff.web.dto;

public record MovimientoFinancieroWebDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 