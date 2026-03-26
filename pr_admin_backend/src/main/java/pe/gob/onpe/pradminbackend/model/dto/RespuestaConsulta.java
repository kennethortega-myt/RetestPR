package pe.gob.onpe.pradminbackend.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaConsulta {
    private boolean resultado;
    private String mensaje;
}
