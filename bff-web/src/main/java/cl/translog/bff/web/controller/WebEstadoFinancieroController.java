package cl.translog.bff.web.controller;

import cl.translog.bff.web.dto.EstadoFinancieroWebDto;
import cl.translog.bff.web.service.WebEstadoFinancieroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/estados-financieros")
public class WebEstadoFinancieroController {

    private final WebEstadoFinancieroService service;

    public WebEstadoFinancieroController(WebEstadoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<EstadoFinancieroWebDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public EstadoFinancieroWebDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<EstadoFinancieroWebDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
