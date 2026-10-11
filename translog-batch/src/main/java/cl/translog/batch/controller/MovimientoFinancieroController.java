package cl.translog.batch.controller;

import cl.translog.batch.domain.MovimientoFinanciero;
import cl.translog.batch.service.MovimientoFinancieroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-financieros")
public class MovimientoFinancieroController {

    private final MovimientoFinancieroService service;

    public MovimientoFinancieroController(MovimientoFinancieroService service) {
        this.service = service;
    }

    
    @GetMapping("/{id}")
    public MovimientoFinanciero getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    @GetMapping("/todos")
    public List<MovimientoFinanciero> getAll() {
        return service.obtenerTodos();
    }

}

