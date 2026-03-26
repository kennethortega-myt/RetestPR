package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoDto {

    private Long id;
    private Long idPadre;
    private String maestro;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
