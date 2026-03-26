
package pe.gob.onpe.pradminbackend.model.bd.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import pe.gob.onpe.pradminbackend.model.dto.RespuestaConsulta;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastWebsocketServiceImpl {


    private final SimpMessagingTemplate messagingTemplate;
    private final CachingConnectionFactory connectionFactory;

    public void broadcastProgressUpdate(String destination, int progress) {
        try {
            messagingTemplate.convertAndSend(destination, progress);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    public void broadcastProgressUpdate(String destination, String jsonPayload) {
        try {
            messagingTemplate.convertAndSend(destination, jsonPayload);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
    
    public RespuestaConsulta validConnectionRabbitMq() {
    	try  (Connection con = connectionFactory.createConnection()) {
            if (con.isOpen()) {
                return RespuestaConsulta.builder().resultado(true).build();
            }
            log.error("connection is not open: " + con);
            return RespuestaConsulta.builder().resultado(false).build();
		} catch (Exception exception) {
            log.error("error validConnectionRabbitMq " + exception.getMessage());
            return RespuestaConsulta.builder().resultado(false).mensaje(exception.getMessage()).build();
		}
    }

}

