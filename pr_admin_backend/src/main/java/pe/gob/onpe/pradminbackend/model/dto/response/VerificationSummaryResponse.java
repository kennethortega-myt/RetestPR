package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationSummaryResponse {
    private VerificationSummarySectionCount signs;
    private VerificationSummarySectionCount votes;
    private VerificationSummarySectionCount observations;
    private VerificationSummarySectionCount datetime;
}
