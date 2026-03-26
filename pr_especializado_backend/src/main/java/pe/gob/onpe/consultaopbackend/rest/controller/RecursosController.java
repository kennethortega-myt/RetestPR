package pe.gob.onpe.consultaopbackend.rest.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.consultaopbackend.recaptcha.properties.RecaptchaProperties;
import pe.gob.onpe.consultaopbackend.security.properties.CifradoProperties;

import java.util.HashMap;
import java.util.Map;


@RestController
@Validated
@RequestMapping("/recurso")
@RequiredArgsConstructor
public class RecursosController {

    private final RecaptchaProperties recaptchaProperties;
    private final CifradoProperties cifradoProperties;

    @Value("${app.consulta.backend}")
    private String urlActaResolucioes;

    @GetMapping(value="/apigoogle", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> apiGoogle() {
        Map<String, String> res = new HashMap<>();
        res.put("api", recaptchaProperties.siteKey());
        res.put("key", cifradoProperties.llavePublica());
        res.put("url", urlActaResolucioes);
        return res;
    }
}
