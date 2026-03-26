package pe.gob.onpe.presentacionbackend.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {
    private String nombreTipoEleccion;
    private String codigoTipoEleccion;
    private String iconoTipoEleccion;
    private Long totalesPorTipoEleccion;
    private String reporteDescarga;
    private List<ActaReporteResponse> actas;
}
