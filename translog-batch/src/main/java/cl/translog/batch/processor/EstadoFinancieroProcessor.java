package cl.translog.batch.processor;

import cl.translog.batch.domain.EstadoFinanciero;
import cl.translog.batch.exception.InvalidEstadoFinancieroException;
import cl.translog.batch.exception.TemporaryEstadoFinancieroException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EstadoFinancieroProcessor implements ItemProcessor<EstadoFinanciero, EstadoFinanciero> {

    private static final Logger log =
            LoggerFactory.getLogger(EstadoFinancieroProcessor.class);

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
    public EstadoFinanciero process(EstadoFinanciero estadoFinanciero) {

        log.info(
                "[{}] Procesando estadoFinanciero {}",
                Thread.currentThread().getName(),
                estadoFinanciero.getCuentaId()
        );

        // Casos borde - Manejo de excepciones
        if (estadoFinanciero.getCuentaId() == null) {

            throw new InvalidEstadoFinancieroException(
                    "Existe Id invalido"
            );
        }

        if (estadoFinanciero.getFecha() == null
                || estadoFinanciero.getFecha().trim().isEmpty()) {

            throw new InvalidEstadoFinancieroException(
                    "Fecha invalida para estadoFinanciero "
                            + estadoFinanciero.getCuentaId()
            );
        }

        if (estadoFinanciero.getMonto() == null) {

            throw new InvalidEstadoFinancieroException(
                    "Monto invalido para estadoFinanciero "
                            + estadoFinanciero.getCuentaId()
            );
        }


        // FORMATEO DE FECHA
        String fechaFormateada =
                formatearFecha(estadoFinanciero.getFecha());

        estadoFinanciero.setFecha(fechaFormateada);


        return estadoFinanciero;
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