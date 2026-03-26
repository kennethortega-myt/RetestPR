package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
public class ImportarPaginadoRequest {
	private Integer numeroPagina;
	private Integer tamanoPagina;
}
