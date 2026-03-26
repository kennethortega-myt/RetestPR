package pe.gob.onpe.presentacionbackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UbigeoDto {
    private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;
    private Integer idUbigeoDistritoElectoral;
}
