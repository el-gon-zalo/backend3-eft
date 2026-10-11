package cl.translog.batch.controller;

import cl.translog.batch.domain.EstadoFinanciero;
import cl.translog.batch.service.EstadoFinancieroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados-financieros")
public class EstadoFinancieroController {

    private final EstadoFinancieroService service;

    public EstadoFinancieroController(EstadoFinancieroService service) {
        this.service = service;
    }

    
    @GetMapping("/{id}")
    public EstadoFinanciero getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    @GetMapping("/todos")
    public List<EstadoFinanciero> getAll() {
        return service.obtenerTodos();
    }

    
    @GetMapping("/cuenta/{cuentaId}")
    public List<EstadoFinanciero> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuentaId(cuentaId);
    }
}

