package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class TokenDTOResponse {

    private boolean success;

    private String message;

    private String token;
}
