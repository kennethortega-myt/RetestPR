package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionDistritalDto {
	private Integer idEleccion;
    private Integer idAmbitoGeografico;
    private String tipoFiltro;
	private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;    
}
