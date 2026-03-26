package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationObservationSectionResponse {
    private String token;

    private VerificationObservation count;
    private VerificationObservation install;
    private VerificationObservation vote;
}
