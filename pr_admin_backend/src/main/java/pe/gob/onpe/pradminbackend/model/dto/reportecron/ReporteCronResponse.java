package pe.gob.onpe.pradminbackend.model.dto.reportecron;

import lombok.*;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCronResponse {
    private GenericResponse<String> response;
}
