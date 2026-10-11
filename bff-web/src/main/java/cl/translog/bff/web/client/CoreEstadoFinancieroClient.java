package cl.translog.bff.web.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import cl.translog.bff.web.dto.EstadoFinancieroDto;

@Component
public class CoreEstadoFinancieroClient {

    private final RestClient restClient;

    public CoreEstadoFinancieroClient(RestClient estadoFinancieroRestClient) {
        this.restClient = estadoFinancieroRestClient;
    }

    public EstadoFinancieroDto estadoFinanciero(Long id) {
        return restClient.get()
                .uri("/api/estados-financieros/{id}", id)
                .retrieve()
                .body(EstadoFinancieroDto.class);
    }

    public List<EstadoFinancieroDto> porCuenta(Integer cuentaId) {
        return restClient.get()
                .uri("/api/estados-financieros/cuenta/{cuentaId}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<EstadoFinancieroDto> todos() {
        return restClient.get()
                .uri("/api/estados-financieros/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}