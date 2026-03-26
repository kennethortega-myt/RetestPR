package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;


@Data
public class VerificationActaResponse {
	private String token;
	private VerificationSignSectionResponse signSection;
	private VerificationVoteSectionResponse voteSection;
	private VerificationObservationSectionResponse observationSection;
	private VerificationDatetimeSectionResponse dateSectionResponse;
}
