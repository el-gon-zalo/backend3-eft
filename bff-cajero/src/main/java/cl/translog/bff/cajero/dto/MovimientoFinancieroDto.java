package cl.translog.bff.cajero.dto;

public record MovimientoFinancieroDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 