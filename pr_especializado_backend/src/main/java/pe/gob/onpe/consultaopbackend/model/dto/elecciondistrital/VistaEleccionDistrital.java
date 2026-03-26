package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaEleccionDistrital {

    private Long id;
    private Integer tipoEleccion;
    private String  tipoFiltro;
    private Integer ambitoGeografico;
    private Integer ubigeoNivel01;
    private Integer ubigeoNivel02;
    private Integer ubigeoNivel03;
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
