package pe.gob.onpe.pradminbackend.mqlistener;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import pe.gob.onpe.pradminbackend.model.bd.service.TramaSceService;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaSceRequest;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaResponse;

@Service
public class RpcTransmisionDataServer {
	
	@Autowired
    private TramaSceService tramaSceService;

	@RabbitListener(queues = "${app.sce-batch-pr.transmision.send-queue}")
    @SendTo // Responde a la cola reply-to especificada en las propiedades del mensaje
    public String receive(String message) throws JsonProcessingException {
		TramaSceRequest request = TramaSceRequest.getObject(message);
		Optional<TramaVistaResponse> response = this.tramaSceService.recibirTrama(request.getTramasSce());
		GenericResponse<TramaVistaResponse> genericResponse = new GenericResponse<>();
		if(response.isPresent()) {
			TramaVistaResponse rpta = response.get();
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(rpta);
		} else {
			genericResponse.setSuccess(Boolean.FALSE);
		}
        return genericResponse.toJson();
    }
	
	
}
