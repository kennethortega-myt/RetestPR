package pe.gob.onpe.presentacionbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AplicacionExcepcion {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> argumentoInvalido(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Solicitud inválida");
        errorMap.put("message", "Los parámetros enviados son incorrectos o están incompletos.");
        return errorMap;
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFound.class)
    public Map<String, String> noencontrado(NotFound ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Recurso no encontrado");
        errorMap.put("message", "La ruta solicitada no existe o es incorrecta.");
        return errorMap;
    }
    

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> parametroInvalido(ConstraintViolationException ex){
        Map<String, String> errorMap = new HashMap<>();
        ex.getConstraintViolations().forEach(error -> errorMap.put(error.getPropertyPath().toString(),
                "Valor: " + error.getInvalidValue() + ", " + error.getMessage()));
        return errorMap;
    }
}
