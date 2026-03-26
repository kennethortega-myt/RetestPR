package pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizacionPoliticaSenadoresDto {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codigoAgrupacionPolitica;


}
