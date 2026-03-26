package pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico;

import lombok.*;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCronResponse {
    private GenericResponse<String> response;
}
