package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaePadronDto {
	private Integer id;
    private String codigoMesa;
    private String documentoIdentidad;
}
