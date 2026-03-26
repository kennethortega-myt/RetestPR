package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import lombok.*;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaResponse {

    private List<TramaVistaDetalleResponse> detalle;
    
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
