package cl.translog.batch.controller;

import cl.translog.batch.domain.Interes;
import cl.translog.batch.service.InteresService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intereses")
public class InteresController {

    private final InteresService service;

    public InteresController(InteresService service) {
        this.service = service;
    }

    
    @GetMapping("/{id}")
    public Interes getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    @GetMapping("/todos")
    public List<Interes> getAll() {
        return service.obtenerTodos();
    }

    
    @GetMapping("/cuenta/{cuentaId}")
    public List<Interes> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuentaId(cuentaId);
    }
}

