package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationSignSectionResponse {
    private String token;

    private VerificationSignItem countPresident;
    private VerificationSignItem countSecretary;
    private VerificationSignItem countThirdMember;

    private VerificationSignItem installPresident;
    private VerificationSignItem installSecretary;
    private VerificationSignItem installThirdMember;

    private VerificationSignItem votePresident;
    private VerificationSignItem voteSecretary;
    private VerificationSignItem voteThirdMember;
    
    private String userStatus;
    private String systemStatus;
    private Integer status;
    
}
