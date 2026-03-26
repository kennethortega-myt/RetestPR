package pe.gob.onpe.presentacionbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ReporteAutomaticoRequestDto {
    private String usuarioConsulta;
    private Integer tipoEleccion;
}
