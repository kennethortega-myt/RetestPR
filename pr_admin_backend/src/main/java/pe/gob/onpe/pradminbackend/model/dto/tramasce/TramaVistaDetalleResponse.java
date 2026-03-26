package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaDetalleResponse {

    private Long  idTransferencia;
    private boolean recibido;
    private String mensaje;
    private List<TramaVistaFilaResponse> filas;
}
