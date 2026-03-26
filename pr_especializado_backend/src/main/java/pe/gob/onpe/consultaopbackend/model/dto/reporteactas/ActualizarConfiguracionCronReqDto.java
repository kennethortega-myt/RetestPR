package pe.gob.onpe.consultaopbackend.model.dto.reporteactas;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ActualizarConfiguracionCronReqDto {
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private String expresionCron;
}
