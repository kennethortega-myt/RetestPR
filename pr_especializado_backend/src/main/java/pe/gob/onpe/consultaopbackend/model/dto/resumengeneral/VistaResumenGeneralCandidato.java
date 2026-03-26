package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaResumenGeneralCandidato {

    private String documentoIdentidad;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private String cargo;
}
