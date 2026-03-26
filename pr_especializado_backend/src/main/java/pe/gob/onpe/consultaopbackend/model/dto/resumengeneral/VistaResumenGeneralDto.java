package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaResumenGeneralDto {

    private Long id;
    private String nombre;
    private Integer idEleccion;
    private Integer totalElectoresHabiles;
    private Integer participacionCiudadana;
    private Double  porcentajeParticipacionCiudadana;
    private Integer totalActas;
    private Integer actasContabilizadas;
    private Double  porcentajeActasContabilizadas;
    private Integer actasObservadasEnviadas;
    private Double  porcentajeActasObservadasEnviadas;
    private Integer actasPendientes;
    private Double  porcentajeActasPendientes;

}
