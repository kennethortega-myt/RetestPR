package pe.gob.onpe.consultaopbackend.model.dto.eleccionpresidencial;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipantePresidencialOrganizacionReporteDto {

    private Long idDetalleUbicacion;
    private String detalleUbicacion;

    private Integer totalVotos;

    private Double votosValidos;


}
