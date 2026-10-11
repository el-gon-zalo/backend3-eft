package cl.translog.bff.mobile.service;

import cl.translog.bff.mobile.client.CoreMovimientoFinancieroClient;
import cl.translog.bff.mobile.dto.MovimientoFinancieroMobileDto;
import cl.translog.bff.mobile.dto.MovimientoFinancieroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileMovimientoFinancieroService {

    private final CoreMovimientoFinancieroClient coreClient;

    public MobileMovimientoFinancieroService(CoreMovimientoFinancieroClient coreClient) {
        this.coreClient = coreClient;
    }

    public MovimientoFinancieroMobileDto obtenerPorId(Long id) {
        MovimientoFinancieroDto dto = coreClient.movimientoFinanciero(id);
        return toMobileDto(dto);
    }

    public List<MovimientoFinancieroMobileDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toMobileDto)
                .toList();
    }


    private MovimientoFinancieroMobileDto toMobileDto(MovimientoFinancieroDto dto) {
        return new MovimientoFinancieroMobileDto(
                dto.id(),
                dto.fecha(),
                dto.monto(),
                dto.tipo()
        );
    }
}
