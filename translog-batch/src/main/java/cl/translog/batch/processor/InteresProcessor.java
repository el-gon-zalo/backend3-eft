package cl.translog.batch.processor;

import cl.translog.batch.domain.Interes;
import cl.translog.batch.exception.InvalidInteresException;
import cl.translog.batch.exception.TemporaryInteresException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InteresProcessor implements ItemProcessor<Interes, Interes> {

    private static final Logger log =
            LoggerFactory.getLogger(InteresProcessor.class);

    
    private final Set<Long> temporaryFailures =
            ConcurrentHashMap.newKeySet();

    @Override
    public Interes process(Interes interes) {

    log.info(
            "[{}] Procesando interes {}",
            Thread.currentThread().getName(),
            interes.getCuentaId()
    );

    // Casos borde - Manejo de excepciones

    if (interes.getCuentaId() == null) {
        throw new InvalidInteresException("Existe Id invalido");
    }

    if (interes.getSaldoOriginal() == null
            || interes.getSaldoOriginal() <= 0) {
        throw new InvalidInteresException(
                "Saldo invalido para interes " + interes.getCuentaId()
        );
    }

    if (interes.getEdad() == null
            || interes.getEdad() <= 0) {
        throw new InvalidInteresException(
                "Edad invalida para interes " + interes.getCuentaId()
        );
    }

    // Cálculo de interés aplicado y saldo final
    // (debe ocurrir ANTES de validar esos mismos campos)

    double tasa;
    if ("AHORRO".equalsIgnoreCase(interes.getTipo())) {
        tasa = 0.05;
    } else if ("PRESTAMO".equalsIgnoreCase(interes.getTipo())) {
        tasa = -0.02;
    } else if ("HIPOTECA".equalsIgnoreCase(interes.getTipo())) {
        tasa = 0.03;
    } else {
        throw new InvalidInteresException(
                "Tipo de interes invalido para interes " + interes.getCuentaId()
        );
    }

    double interesNum = interes.getSaldoOriginal() * tasa;
    interes.setInteresAplicado(interesNum);
    interes.setSaldoFinal(interes.getSaldoOriginal() + interesNum);

    // Validación posterior al cálculo

    if (interes.getSaldoFinal() == null || interes.getSaldoFinal() <= 0) {
        throw new InvalidInteresException(
                "Saldo final invalido para interes " + interes.getCuentaId()
        );
    }

    return interes;
}
}
