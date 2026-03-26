package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransmisionRecepcionDto {

    private Long id;
    private String proceso;
    private Long idCentroComputo;
    private String tramaDato;
    private String tramaImagen;
    private Integer estado;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
