package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationVoteItem {
    private Integer position;
    private String fileId;
    private String  systemValue;
    private String  userValue;
}
