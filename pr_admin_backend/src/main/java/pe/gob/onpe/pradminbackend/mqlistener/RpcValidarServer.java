package pe.gob.onpe.pradminbackend.mqlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RpcValidarServer {
	
	@RabbitListener(queues = "validar")
	@SendTo
	public void receive(String message) {
		log.info("Mensaje recibido: "+message);
	}
}
