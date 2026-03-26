package pe.gob.onpe.consultaopbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ReporteFileDTO {

    private String nombreArchivo;
    private byte[] archivo;
}
