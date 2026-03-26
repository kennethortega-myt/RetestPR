package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbigeoDto {

    private Long id;
    private Long idPadre;
    private Long idAmbitoElectoral;

    private Long idCentroComputo;

    private Integer idDistritoElectoral;
    private String  departamento;
	private String  provincia;
	private String  distrito;
    private String codigo; // ubigeo
    private String nombre;
    private Integer tipoAmbitoGeografico;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
