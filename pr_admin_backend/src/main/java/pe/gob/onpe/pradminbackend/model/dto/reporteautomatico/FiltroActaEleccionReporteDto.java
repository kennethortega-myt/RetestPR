package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroActaEleccionReporteDto {
    private Integer idEleccion;
    private Integer idAmbitoGeografico;
    private String tipoFiltro;

    private Integer idUbigeoDepartamento;
    private Integer idUbigeoProvincia;
    private Integer idUbigeoDistrito;
    private Integer idDistritoElectoral;

    private String codigoOp;


}
