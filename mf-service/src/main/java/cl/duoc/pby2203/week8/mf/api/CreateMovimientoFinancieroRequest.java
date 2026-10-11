package cl.duoc.pby2203.week8.mf.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovimientoFinancieroRequest(
        @NotBlank String fecha,
        @NotNull @Positive Double monto,
        @NotBlank String tipo
) {}