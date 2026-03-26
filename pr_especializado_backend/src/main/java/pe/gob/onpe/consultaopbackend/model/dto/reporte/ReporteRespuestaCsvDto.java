package pe.gob.onpe.consultaopbackend.model.dto.reporte;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRespuestaCsvDto {

    private List<ActasResponseReporteDto> registrosReporte;
    private List<String> listaOrgPolitica;
}
