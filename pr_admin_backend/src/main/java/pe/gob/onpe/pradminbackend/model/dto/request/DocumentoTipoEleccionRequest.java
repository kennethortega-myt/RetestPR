package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoTipoEleccionRequest {

    private Long id;

    private TipoEleccionRequest tipoEleccion;

    private DocumentoElectoralRequest documentoElectoral;

    private String requerido;

}
