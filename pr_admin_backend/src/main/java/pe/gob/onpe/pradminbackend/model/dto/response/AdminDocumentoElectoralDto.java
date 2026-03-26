package pe.gob.onpe.pradminbackend.model.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class AdminDocumentoElectoralDto {


	private String nombreDoc;
	private List<PropiedadDocumentoElectoralDto> documentos;
	
}
