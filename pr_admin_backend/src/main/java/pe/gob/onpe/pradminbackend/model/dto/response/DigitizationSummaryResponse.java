package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class DigitizationSummaryResponse {
    private Integer pending=0;
    private Integer approved=0;
    private Integer rejected=0;
}
