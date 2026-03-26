package pe.gob.onpe.pradminbackend.model.dto.request;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.pradminbackend.model.dto.ArchivoTransmisionDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ArchivoTransmisionRequest {

	private Long idActa;
	private List<ArchivoTransmisionDto> archivos;
	
	public static ArchivoTransmisionRequest getObject(String jsonString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, ArchivoTransmisionRequest.class);
	}
	
}
