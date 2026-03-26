package pe.gob.onpe.consultaopbackend.model.dto.reporte;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EstadoServicioFirmaDto {
    private boolean estado;
    private String mensaje;
}
