package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaFilaResponse {

    private Integer  idFila;
    private boolean recibido;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensaje;
}
