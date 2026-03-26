package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoElectoralDto {

    private Long id;
    private String nombre;
    private String acronimo;
    private String fechaConvocatoria;
    private Long tipoAmbitoElectoral;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
