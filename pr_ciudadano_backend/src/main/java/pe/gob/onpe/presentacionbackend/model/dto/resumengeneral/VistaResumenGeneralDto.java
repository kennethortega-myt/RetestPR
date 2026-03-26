package pe.gob.onpe.presentacionbackend.model.dto.resumengeneral;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaResumenGeneralDto {

    private Long id;
    private String nombre;

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
    
    //revocatoria
    private Integer ubigeoNivel01;
    private Integer ubigeoNivel02;
    private Integer ubigeoNivel03;
    private String ubigeoDesc;

}
