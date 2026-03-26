package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionDto {

    private Long id;
    private Long idProcesoElectoral;
    private String nombreVista;
    private Integer principal;
    private String codigo;
    private String nombre;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
