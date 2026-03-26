package pe.gob.onpe.pradminbackend.rest.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseEntity<>("Ruta no encontrada. Esta URL no está disponible en la API.", HttpStatus.NOT_FOUND);
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return new ResponseEntity<>("Acceso denegado. No tienes permisos para acceder aquí.", HttpStatus.FORBIDDEN);
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return new ResponseEntity<>("No autenticado. Inicia sesión para acceder a este recurso.", HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Ha ocurrido un error inesperado.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}