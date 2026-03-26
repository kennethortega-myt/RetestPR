package pe.gob.onpe.pradminbackend.mqlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ColaService {


    private final AmqpAdmin amqpAdmin;

    private void purgarCola(String nombreCola) {
        amqpAdmin.purgeQueue(nombreCola, true);
        log.info("Cola purgada: " + nombreCola);
    }

    public void eliminarMensajesCola(){
        try {
            getColas().forEach(this::purgarCola);
            log.info("Colas limpiadas");
        } catch(Exception error) {
            log.error("Error eliminarMensajesCola: ", error);
        }
    }

    private List<String> getColas(){
        return List.of("requestpr.queue","responsepr.queue","requestpr.queue.file","responsepr.queue.file");
    }

}
