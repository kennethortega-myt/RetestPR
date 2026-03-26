package pe.gob.onpe.consultaopbackend.security.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class ExceptionResponse {
    private String mensaje;
    private String mensajeInteno;
    private String requestedURI;
    private List<FieldErrorModel> errorsField;

    private int estado;
    private String metodo;
    private String clase;
    private String lineaCodigoError;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private int resultado;

}
