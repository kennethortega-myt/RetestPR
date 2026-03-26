package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRespuestaDto {

    private List<ActasResponseReporteDto> registrosReporte;
    private List<String> listaOrgPolitica;
}
