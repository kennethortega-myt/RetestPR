package pe.gob.onpe.pradminbackend.model.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class VerificationVoteSectionResponse {
    private String token;
    private List<VerificationVoteItem> items;
}
