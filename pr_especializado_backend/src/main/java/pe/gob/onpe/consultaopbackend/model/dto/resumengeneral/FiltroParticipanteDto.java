package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroParticipanteDto {

    private Integer idUbigeoDepartamento;
    private Integer idUbigeoProvincia;
    private Integer idUbigeoDistrito;
    private Integer idDistritoElectoral;
    
    private Integer idEleccion;
    private Integer idAmbitoGeografico;
    private String tipoFiltro;
    
}
