package cl.translog.bff.mobile.controller;

import cl.translog.bff.mobile.dto.EstadoFinancieroMobileDto;
import cl.translog.bff.mobile.service.MobileEstadoFinancieroService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobile/estados-financieros")
public class MobileEstadoFinancieroController {

    private final MobileEstadoFinancieroService service;

    public MobileEstadoFinancieroController(MobileEstadoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<EstadoFinancieroMobileDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public EstadoFinancieroMobileDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<EstadoFinancieroMobileDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
