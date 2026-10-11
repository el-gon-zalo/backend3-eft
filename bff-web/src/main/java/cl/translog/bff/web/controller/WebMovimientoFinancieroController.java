package cl.translog.bff.web.controller;

import cl.translog.bff.web.dto.MovimientoFinancieroWebDto;
import cl.translog.bff.web.service.WebMovimientoFinancieroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/movimientos-financieros")
public class WebMovimientoFinancieroController {

    private final WebMovimientoFinancieroService service;

    public WebMovimientoFinancieroController(WebMovimientoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<MovimientoFinancieroWebDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public MovimientoFinancieroWebDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
