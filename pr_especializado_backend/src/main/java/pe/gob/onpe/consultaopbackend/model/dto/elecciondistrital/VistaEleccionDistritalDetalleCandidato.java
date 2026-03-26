package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaEleccionDistritalDetalleCandidato {
	private String documentoIdentidad;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private String cargo;
}
