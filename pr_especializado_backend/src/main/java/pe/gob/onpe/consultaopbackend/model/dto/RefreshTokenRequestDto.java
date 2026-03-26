package pe.gob.onpe.consultaopbackend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class RefreshTokenRequestDto {
    @NotEmpty
    private String refreshToken;
}
