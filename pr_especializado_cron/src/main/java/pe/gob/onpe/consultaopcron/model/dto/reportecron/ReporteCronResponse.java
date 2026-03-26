package pe.gob.onpe.consultaopcron.model.dto.reportecron;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCronResponse {
    private GenericResponse<String> response;
}
