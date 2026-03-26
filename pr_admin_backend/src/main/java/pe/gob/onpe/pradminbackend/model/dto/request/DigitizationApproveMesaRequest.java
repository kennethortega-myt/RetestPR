package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.Data;

@Data
public class DigitizationApproveMesaRequest {
    private Long actaId;
    private String fileId1;
    private String fileId2;
    private String estado;
}
