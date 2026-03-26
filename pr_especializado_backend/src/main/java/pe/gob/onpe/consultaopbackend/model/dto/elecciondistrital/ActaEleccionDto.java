package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ActaEleccionDto {

    private Double actasContabilizadas;
    private Integer totalActas;
    private Double participacionCiudadana;
    private Double actasEnviadasJee;
    private Double actasPendientes;
    private Date fechaActualizacion;
    private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;
}
