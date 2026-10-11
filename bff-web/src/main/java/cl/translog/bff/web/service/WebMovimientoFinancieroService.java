package cl.translog.bff.web.service;

import cl.translog.bff.web.client.CoreMovimientoFinancieroClient;
import cl.translog.bff.web.dto.MovimientoFinancieroWebDto;
import cl.translog.bff.web.dto.MovimientoFinancieroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebMovimientoFinancieroService {

    private final CoreMovimientoFinancieroClient coreClient;

    public WebMovimientoFinancieroService(CoreMovimientoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public MovimientoFinancieroWebDto obtenerPorId(Long id) {
        MovimientoFinancieroDto dto = coreClient.movimientoFinanciero(id);
        return toWebDto(dto);
    }

    public List<MovimientoFinancieroWebDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toWebDto)
                .toList();
    }


    private MovimientoFinancieroWebDto toWebDto(MovimientoFinancieroDto dto) {
        return new MovimientoFinancieroWebDto(
                dto.id(),
                dto.fecha(),
                dto.monto(),
                dto.tipo(),
                dto.estado()
        );
    }
}
