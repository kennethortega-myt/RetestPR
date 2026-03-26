package pe.gob.onpe.consultaopbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DownloadActaException extends RuntimeException{
    public DownloadActaException(String message) {
        super(message);
    }
}
