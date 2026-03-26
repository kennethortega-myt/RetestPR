package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TramaScePuestaCeroDto {

    private boolean limpiarCarpetaDescargaActas;
    private String mensaje;

}
