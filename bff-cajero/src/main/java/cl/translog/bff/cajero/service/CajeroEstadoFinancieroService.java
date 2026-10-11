package cl.translog.bff.cajero.service;

import cl.translog.bff.cajero.client.CoreEstadoFinancieroClient;
import cl.translog.bff.cajero.dto.EstadoFinancieroDto;
import cl.translog.bff.cajero.dto.EstadoFinancieroCajeroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CajeroEstadoFinancieroService {

    private final CoreEstadoFinancieroClient coreClient;

    public CajeroEstadoFinancieroService(CoreEstadoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public EstadoFinancieroCajeroDto obtenerPorId(Long id) {
        EstadoFinancieroDto dto = coreClient.estadoFinanciero(id);
        return toCajeroDto(dto);
    }

    public List<EstadoFinancieroCajeroDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toCajeroDto)
                .toList();
    }

    public List<EstadoFinancieroCajeroDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toCajeroDto)
                .toList();
    }

    private EstadoFinancieroCajeroDto toCajeroDto(EstadoFinancieroDto dto) {
        return new EstadoFinancieroCajeroDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha()
        );
    }
}
