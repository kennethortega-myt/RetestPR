package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

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
