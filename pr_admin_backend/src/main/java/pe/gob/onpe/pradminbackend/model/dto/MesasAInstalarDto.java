package pe.gob.onpe.pradminbackend.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class MesasAInstalarDto {

	private String titulo;
	private Long   cantidad;
	private List<MesasAInstalarDetalleDto> detalle;
}
