package pe.gob.onpe.consultaopbackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Componente de configuración para inyectar propiedades relacionadas con el cifrado.
 * Usado para obtener la clave secreta desde el archivo de propiedades de Spring.
 * * NOTA: Esta clase es un 'record', disponible a partir de Java 16.
 */
@Component
public record CypherProperties(
    // Inyecta el valor de la propiedad 'nrodocumento.cipherkey'
    @Value("${nrodocumento.cipherkey}")
    String cipherKey
) {}
