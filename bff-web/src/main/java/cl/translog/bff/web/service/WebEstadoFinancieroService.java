package cl.translog.bff.web.service;

import cl.translog.bff.web.client.CoreEstadoFinancieroClient;
import cl.translog.bff.web.dto.EstadoFinancieroDto;
import cl.translog.bff.web.dto.EstadoFinancieroWebDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebEstadoFinancieroService {

    private final CoreEstadoFinancieroClient coreClient;

    public WebEstadoFinancieroService(CoreEstadoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public EstadoFinancieroWebDto obtenerPorId(Long id) {
        EstadoFinancieroDto dto = coreClient.estadoFinanciero(id);
        return toWebDto(dto);
    }

    public List<EstadoFinancieroWebDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toWebDto)
                .toList();
    }

    public List<EstadoFinancieroWebDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toWebDto)
                .toList();
    }

    private EstadoFinancieroWebDto toWebDto(EstadoFinancieroDto dto) {
        return new EstadoFinancieroWebDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha(),
                dto.transaccion(),
                dto.monto(),
                dto.descripcion()
        );
    }
}
