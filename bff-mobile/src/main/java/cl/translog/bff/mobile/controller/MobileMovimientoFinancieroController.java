package cl.translog.bff.mobile.controller;

import cl.translog.bff.mobile.dto.MovimientoFinancieroMobileDto;
import cl.translog.bff.mobile.service.MobileMovimientoFinancieroService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobile/movimientos-financieros")
public class MobileMovimientoFinancieroController {

    private final MobileMovimientoFinancieroService service;

    public MobileMovimientoFinancieroController(MobileMovimientoFinancieroService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<MovimientoFinancieroMobileDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public MovimientoFinancieroMobileDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
