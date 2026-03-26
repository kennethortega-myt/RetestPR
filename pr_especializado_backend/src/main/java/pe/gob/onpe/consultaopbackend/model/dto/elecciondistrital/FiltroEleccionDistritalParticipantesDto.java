package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionDistritalParticipantesDto {

	private Integer idEleccion;
    private Integer idAmbitoGeografico;
    private String tipoFiltro;
	private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;
	private String agrupacionPolitica;
    private String nombreCandidato;
    
}
