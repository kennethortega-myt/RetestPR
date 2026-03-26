package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

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
