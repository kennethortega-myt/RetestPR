package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.Data;
import pe.gob.onpe.pradminbackend.model.dto.ActaContabilizadaDetalleDto;

import java.util.List;

@Data
public class ActasPorProcesarDto {

	private String titulo;
	private List<ActaContabilizadaDetalleDto> detalle;
}
