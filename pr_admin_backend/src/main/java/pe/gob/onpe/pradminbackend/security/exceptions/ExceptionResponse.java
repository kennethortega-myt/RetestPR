package pe.gob.onpe.pradminbackend.security.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ExceptionResponse {
    String mensaje;
    String mensajeInteno;
    String requestedURI;
    List<FieldErrorModel> errorsField;

    int estado;
    String metodo;
    String clase;
    String lineaCodigoError;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    LocalDateTime timestamp;
    int resultado;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensajeInteno() {
        return mensajeInteno;
    }

    public void setMensajeInteno(String mensajeInteno) {
        this.mensajeInteno = mensajeInteno;
    }

    public String getRequestedURI() {
        return requestedURI;
    }

    public void setRequestedURI(String requestedURI) {
        this.requestedURI = requestedURI;
    }

    public List<FieldErrorModel> getErrorsField() {
        return errorsField;
    }

    public void setErrorsField(List<FieldErrorModel> errorsField) {
        this.errorsField = errorsField;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getLineaCodigoError() {
        return lineaCodigoError;
    }

    public void setLineaCodigoError(String lineaCodigoError) {
        this.lineaCodigoError = lineaCodigoError;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }
}
