package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationSignItem {
    private String fileId;
    private String systemStatus;
    private String userStatus;
}
