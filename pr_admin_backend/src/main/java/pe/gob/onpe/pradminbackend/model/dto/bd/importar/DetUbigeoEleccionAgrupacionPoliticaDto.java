package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetUbigeoEleccionAgrupacionPoliticaDto {

    private Long id;
    private Long idDetUbigeoEleccion;
    private Long idAgrupacionPolitica;
    private Integer posicion;
    private Integer estado;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
