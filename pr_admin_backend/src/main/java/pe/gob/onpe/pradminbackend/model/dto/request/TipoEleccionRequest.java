package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoEleccionRequest {

    private Integer id;
    private String nombre;
    private Integer activo;

}
