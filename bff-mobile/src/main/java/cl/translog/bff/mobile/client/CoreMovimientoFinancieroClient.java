package cl.translog.bff.mobile.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.translog.bff.mobile.dto.MovimientoFinancieroDto;

@Component
public class CoreMovimientoFinancieroClient {

    private final RestClient restClient;

    public CoreMovimientoFinancieroClient(RestClient movimientoFinancieroRestClient) {
        this.restClient = movimientoFinancieroRestClient;
    }

    public MovimientoFinancieroDto movimientoFinanciero(Long id) {
        return restClient.get()
                .uri("/api/movimientos-financieros/{id}", id)
                .retrieve()
                .body(MovimientoFinancieroDto.class);
    }

    public List<MovimientoFinancieroDto> todos() {
        return restClient.get()
                .uri("/api/movimientos-financieros/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}