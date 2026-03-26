package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;
import pe.gob.onpe.pradminbackend.model.dto.RespuestaConsulta;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResumenGeneralServiceImpl implements ResumenGeneralService {

    private final BroadcastWebsocketServiceImpl broadcastWebsocketService;

	@Override
	public EstadoServicioDto validarServicioRabitmq() {

		RespuestaConsulta consulta = broadcastWebsocketService.validConnectionRabbitMq();

		return EstadoServicioDto.builder()
				.estado(consulta.isResultado())
				.mensaje(consulta.isResultado() ? "Disponible" : "No disponible")
				.build();


	}

}
 