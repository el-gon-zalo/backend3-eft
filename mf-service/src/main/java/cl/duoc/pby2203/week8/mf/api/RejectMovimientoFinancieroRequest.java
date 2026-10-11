package cl.duoc.pby2203.week8.mf.api;

import jakarta.validation.constraints.NotBlank;

public record RejectMovimientoFinancieroRequest(
        @NotBlank String reason
) {
}