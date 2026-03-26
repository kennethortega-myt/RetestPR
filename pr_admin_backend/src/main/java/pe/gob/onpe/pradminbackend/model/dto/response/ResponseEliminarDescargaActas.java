package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ResponseEliminarDescargaActas {

    private boolean limpiarCarpetaDescargaActas;
    private String mensaje;

}
