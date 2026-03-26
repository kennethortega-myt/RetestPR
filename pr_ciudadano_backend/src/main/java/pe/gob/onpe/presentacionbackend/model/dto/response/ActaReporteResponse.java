package pe.gob.onpe.presentacionbackend.model.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaReporteResponse {
    private String idActa;
    private Date fechaActa;
    private Double porcentaje;
    private String rutaActa;
    private Integer estadoActa;
}
