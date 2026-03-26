package pe.gob.onpe.presentacionbackend.rest.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("${server.error.path:/error}")
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    public GlobalErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        var webRequest = new ServletWebRequest(request);
        var attributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        int statusCode = (int) attributes.getOrDefault("status", 500);
        HttpStatus status = HttpStatus.valueOf(statusCode);

        Map<String, Object> response = new HashMap<>();
        response.put("status", statusCode);

        switch (status) {
            case NOT_FOUND -> {
                response.put(ERROR, "Recurso no encontrado");
                response.put(MESSAGE, "La ruta solicitada no existe o es incorrecta.");
            }
            case BAD_REQUEST -> {
                response.put(ERROR, "Solicitud inválida");
                response.put(MESSAGE, "Los parámetros enviados son incorrectos o están incompletos.");
            }
            default -> {
                response.put(ERROR, "Error interno del servidor");
                response.put(MESSAGE, "Ocurrió un error inesperado. Inténtelo más tarde.");
            }
        }

        return ResponseEntity.status(status).body(response);
    }
}