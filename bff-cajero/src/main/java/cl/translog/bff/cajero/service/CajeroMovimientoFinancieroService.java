package cl.translog.bff.cajero.service;

import cl.translog.bff.cajero.client.CoreMovimientoFinancieroClient;
import cl.translog.bff.cajero.dto.MovimientoFinancieroCajeroDto;
import cl.translog.bff.cajero.dto.MovimientoFinancieroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CajeroMovimientoFinancieroService {

    private final CoreMovimientoFinancieroClient coreClient;

    public CajeroMovimientoFinancieroService(CoreMovimientoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public MovimientoFinancieroCajeroDto obtenerPorId(Long id) {
        MovimientoFinancieroDto dto = coreClient.movimientoFinanciero(id);
        return toCajeroDto(dto);
    }

    public List<MovimientoFinancieroCajeroDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toCajeroDto)
                .toList();
    }


    private MovimientoFinancieroCajeroDto toCajeroDto(MovimientoFinancieroDto dto) {
        return new MovimientoFinancieroCajeroDto(
                dto.id(),
                dto.fecha(),
                dto.monto()
        );
    }
}
