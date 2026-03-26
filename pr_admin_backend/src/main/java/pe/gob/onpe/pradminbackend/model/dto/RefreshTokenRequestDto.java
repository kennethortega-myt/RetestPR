package pe.gob.onpe.pradminbackend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotEmpty
    private String refreshToken;
}
