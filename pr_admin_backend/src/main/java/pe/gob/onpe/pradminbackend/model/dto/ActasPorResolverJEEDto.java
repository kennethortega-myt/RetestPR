package pe.gob.onpe.pradminbackend.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class ActasPorResolverJEEDto {

	private String titulo;
	private List<ActaContabilizadaDetalleDto> detalle;
}
