package cl.duoc.pby2203.week8.mf.api;

import cl.duoc.pby2203.week8.mf.domain.MovimientoFinanciero;
import cl.duoc.pby2203.week8.mf.repository.MovimientoFinancieroRepository;
import cl.duoc.pby2203.week8.mf.service.MovimientoFinancieroApplicationService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos-financieros")
public class MovimientoFinancieroController {

    private final MovimientoFinancieroApplicationService service;
    private final MovimientoFinancieroRepository repository;

    public MovimientoFinancieroController(
            MovimientoFinancieroApplicationService service,
            MovimientoFinancieroRepository repository) {

        this.service = service;
        this.repository = repository;
    }

    /**
     * Crear un nueva movimientoFinanciero.
     *
     * POST /api/movimientos-financieros
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody CreateMovimientoFinancieroRequest request) {

        MovimientoFinanciero movimientoFinanciero = service.create(request);

        /*
         * 202 Accepted comunica que la solicitud fue aceptada,
         * pero el procesamiento asíncrono continúa mediante Kafka.
         */
        return ResponseEntity.accepted()
                .location(
                        URI.create(
                                "/api/movimientos-financieros/"
                                        + movimientoFinanciero.getMovimientoFinancieroId()
                        )
                )
                .body(
                        Map.of(
                                "movimientoFinancieroId",
                                movimientoFinanciero.getMovimientoFinancieroId(),
                                "status",
                                movimientoFinanciero.getStatus()
                        )
                );
    }

    /**
     * Obtener un movimientoFinanciero por ID.
     *
     * GET /api/movimientos-financieros/{movimientoFinancieroId}
     */
    @GetMapping("/{movimientoFinancieroId}")
    public ResponseEntity<MovimientoFinanciero> find(
            @PathVariable String movimientoFinancieroId) {

        return repository.findById(movimientoFinancieroId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    /**
     * Obtener todas las movimientos-financieros.
     *
     * GET /api/movimientos-financieros
     */
    @GetMapping
    public Collection<MovimientoFinanciero> findAll() {

        return repository.findAll();
    }

    /**
     * Solicitar aprobación de un movimientoFinanciero.
     *
     * POST /api/movimientos-financieros/{movimientoFinancieroId}/approve
     */
    @PostMapping("/{movimientoFinancieroId}/approve")
    public ResponseEntity<Map<String, String>> approve(
            @PathVariable String movimientoFinancieroId) {

        service.approve(movimientoFinancieroId);

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "movimientoFinancieroId",
                                movimientoFinancieroId,
                                "message",
                                "MovimientoFinancieroApproved event published"
                        )
                );
    }

    /**
     * Solicitar rechazo de un movimientoFinanciero.
     *
     * POST /api/movimientos-financieros/{movimientoFinancieroId}/reject
     */
    @PostMapping("/{movimientoFinancieroId}/reject")
    public ResponseEntity<Map<String, String>> reject(
            @PathVariable String movimientoFinancieroId,
            @Valid @RequestBody RejectMovimientoFinancieroRequest request) {

        service.reject(
                movimientoFinancieroId,
                request.reason()
        );

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "movimientoFinancieroId",
                                movimientoFinancieroId,
                                "message",
                                "MovimientoFinancieroRejected event published"
                        )
                );
    }

    /**
     * Republicar el evento MovimientoFinancieroCreated original.
     *
     * POST /api/movimientos-financieros/{movimientoFinancieroId}/replay-created
     */
    @PostMapping("/{movimientoFinancieroId}/replay-created")
    public ResponseEntity<Map<String, String>> replay(
            @PathVariable String movimientoFinancieroId) {

        service.replayCreated(movimientoFinancieroId);

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "message",
                                "MovimientoFinancieroCreated replayed with the original eventId"
                        )
                );
    }
}