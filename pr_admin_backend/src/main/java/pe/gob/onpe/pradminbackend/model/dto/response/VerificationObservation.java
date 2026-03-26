package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationObservation {
    private String fileId;
    private String systemValue;
    private String userValue;
    private boolean nullityRequest;
}
