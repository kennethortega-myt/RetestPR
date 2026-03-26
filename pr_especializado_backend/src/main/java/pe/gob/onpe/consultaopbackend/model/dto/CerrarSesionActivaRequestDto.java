package pe.gob.onpe.consultaopbackend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CerrarSesionActivaRequestDto {
    @NotEmpty
    private String usuario;
}
