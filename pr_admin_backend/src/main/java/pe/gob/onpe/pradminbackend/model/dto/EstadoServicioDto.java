package pe.gob.onpe.pradminbackend.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EstadoServicioDto {
    private boolean estado;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensaje;
}
