package cl.translog.bff.mobile.service;

import cl.translog.bff.mobile.client.CoreEstadoFinancieroClient;
import cl.translog.bff.mobile.dto.EstadoFinancieroDto;
import cl.translog.bff.mobile.dto.EstadoFinancieroMobileDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileEstadoFinancieroService {

    private final CoreEstadoFinancieroClient coreClient;

    public MobileEstadoFinancieroService(CoreEstadoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public EstadoFinancieroMobileDto obtenerPorId(Long id) {
        EstadoFinancieroDto dto = coreClient.estadoFinanciero(id);
        return toMobileDto(dto);
    }

    public List<EstadoFinancieroMobileDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toMobileDto)
                .toList();
    }

    public List<EstadoFinancieroMobileDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toMobileDto)
                .toList();
    }

    private EstadoFinancieroMobileDto toMobileDto(EstadoFinancieroDto dto) {
        return new EstadoFinancieroMobileDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha(),
                dto.transaccion()
        );
    }
}
