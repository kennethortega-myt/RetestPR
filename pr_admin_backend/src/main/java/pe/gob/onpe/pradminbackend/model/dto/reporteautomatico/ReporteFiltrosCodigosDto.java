package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteFiltrosCodigosDto {

    private Integer tipoReporte;
    private Integer tipoEleccion;
    private Integer ambitoGeografico;

    private Integer ubigeoNivel1;
    private Integer ubigeoNivel2;
    private Integer ubigeoNivel3;
    private Integer localVotacion;
    private Integer distritoElectoral;

}
