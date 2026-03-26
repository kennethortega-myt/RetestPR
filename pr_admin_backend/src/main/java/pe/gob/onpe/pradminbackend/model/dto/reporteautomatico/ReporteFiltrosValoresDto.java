package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;


@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFiltrosValoresDto {

    private String tipoReporte;
    private String tipoEleccion;
    private String ambitoGeografico;

    private String ubigeoNivel1;
    private String ubigeoNivel2;
    private String ubigeoNivel3;
    private String localVotacion;

}
