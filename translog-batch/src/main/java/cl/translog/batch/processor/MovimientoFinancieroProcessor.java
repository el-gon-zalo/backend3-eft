package cl.translog.batch.processor;

import cl.translog.batch.domain.MovimientoFinanciero;
import cl.translog.batch.exception.InvalidMovimientoFinancieroException;
import cl.translog.batch.exception.TemporaryMovimientoFinancieroException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MovimientoFinancieroProcessor implements ItemProcessor<MovimientoFinanciero, MovimientoFinanciero> {

    private static final Logger log =
            LoggerFactory.getLogger(MovimientoFinancieroProcessor.class);

    // Formatos de fecha
    private static final DateTimeFormatter FORMATO_SALIDA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter FORMATO_DD_MM_GUION =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter FORMATO_DD_MM_SLASH =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_YYYY_MM_SLASH =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter FORMATO_YYYY_MM_GUION =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Set<Long> temporaryFailures =
            ConcurrentHashMap.newKeySet();


    @Override
    public MovimientoFinanciero process(MovimientoFinanciero movimientoFinanciero) {

        log.info(
                "[{}] Procesando movimientoFinanciero {}",
                Thread.currentThread().getName(),
                movimientoFinanciero.getId()
        );

        // Casos borde - Manejo de excepciones
        if (movimientoFinanciero.getId() == null) {

            throw new InvalidMovimientoFinancieroException(
                    "Existe Id invalido"
            );
        }

        if (movimientoFinanciero.getFecha() == null
                || movimientoFinanciero.getFecha().trim().isEmpty()) {

            throw new InvalidMovimientoFinancieroException(
                    "Fecha invalida para movimientoFinanciero "
                            + movimientoFinanciero.getId()
            );
        }

        if (movimientoFinanciero.getMonto() == null || movimientoFinanciero.getMonto() < 0) {

            throw new InvalidMovimientoFinancieroException(
                    "Monto invalido para movimientoFinanciero "
                            + movimientoFinanciero.getId()
            );
        }

        // Estado: detectar anomalia
        if (movimientoFinanciero.getMonto() > 1000000) {
            movimientoFinanciero.setEstado("MONTO_ANOMALO");
        } else {
            movimientoFinanciero.setEstado("MONTO_NORMAL");
        }


        // FORMATEO DE FECHA
        String fechaFormateada =
                formatearFecha(movimientoFinanciero.getFecha());

        movimientoFinanciero.setFecha(fechaFormateada);


        return movimientoFinanciero;
    }


    private static String formatearFecha(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return "";
        }

        String valor = fecha.trim();

        LocalDate fechaNormalizada;

        if (valor.matches("\\d{2}-\\d{2}-\\d{4}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_DD_MM_GUION);

        } else if (valor.matches("\\d{2}/\\d{2}/\\d{4}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_DD_MM_SLASH);

        } else if (valor.matches("\\d{4}/\\d{2}/\\d{2}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_YYYY_MM_SLASH);

        } else if (valor.matches("\\d{4}-\\d{2}-\\d{2}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_YYYY_MM_GUION);

        } else {

            throw new IllegalArgumentException(
                    "Formato de fecha no reconocido: " + valor
            );
        }

        return fechaNormalizada.format(FORMATO_SALIDA);
    }
}