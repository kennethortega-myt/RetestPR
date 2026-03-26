package pe.gob.onpe.pradminbackend.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record CrossOriginProperties 
		(@Value("${crossorigin.habilitado}")
        String habilitado,
        @Value("${crossorigin.urls}")
        String urls)
{}
		 
