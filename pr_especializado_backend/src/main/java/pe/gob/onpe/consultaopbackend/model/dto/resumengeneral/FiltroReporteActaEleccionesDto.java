package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroReporteActaEleccionesDto {
    private List<Integer> idElecciones;
    private String tipoFiltro;
    private Integer idAmbitoGeografico;
    private Integer idUbigeoDepartamento;
    private Integer idUbigeoProvincia;
    private Integer idUbigeoDistrito;
    private Integer idDistritoElectoral;

}
