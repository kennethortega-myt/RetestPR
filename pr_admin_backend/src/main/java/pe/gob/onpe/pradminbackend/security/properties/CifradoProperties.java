package pe.gob.onpe.pradminbackend.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record CifradoProperties
        (@Value("${public.key}")
         String llavePublica,
         @Value("${private.key}")
         String llavePrivada)
{}
