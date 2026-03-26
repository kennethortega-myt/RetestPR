package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoreoListActaItem {
    private Long actaId;
    private String mesa;
    private String estado;
    private String fecha;
    private String imagenEscrutinio;
    private String imagenInstalacion;
}
