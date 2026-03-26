package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleccionDistritalDto {
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
    private List<VistaEleccionDistritalDetalle> detalle;
}
