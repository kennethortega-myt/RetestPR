package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.Data;

@Data
public class DigitizationRejectMesaRequest {
    private Long actaId;
    private int type;
    private String fileId;
    private String comments;
}
