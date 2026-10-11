package cl.translog.bff.cajero.controller;

import cl.translog.bff.cajero.dto.EstadoFinancieroCajeroDto;
import cl.translog.bff.cajero.service.CajeroEstadoFinancieroService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cajero/estados-financieros")
public class CajeroEstadoFinancieroController {

    private final CajeroEstadoFinancieroService service;

    public CajeroEstadoFinancieroController(CajeroEstadoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<EstadoFinancieroCajeroDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public EstadoFinancieroCajeroDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<EstadoFinancieroCajeroDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
