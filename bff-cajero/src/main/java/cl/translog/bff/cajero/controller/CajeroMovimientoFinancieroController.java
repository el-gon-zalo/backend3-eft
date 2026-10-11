package cl.translog.bff.cajero.controller;

import cl.translog.bff.cajero.dto.MovimientoFinancieroCajeroDto;
import cl.translog.bff.cajero.service.CajeroMovimientoFinancieroService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cajero/movimientos-financieros")
public class CajeroMovimientoFinancieroController {

    private final CajeroMovimientoFinancieroService service;

    public CajeroMovimientoFinancieroController(CajeroMovimientoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<MovimientoFinancieroCajeroDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public MovimientoFinancieroCajeroDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
