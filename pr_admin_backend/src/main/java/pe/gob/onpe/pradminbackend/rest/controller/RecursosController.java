package pe.gob.onpe.pradminbackend.rest.controller;


import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.pradminbackend.recaptcha.properties.RecaptchaProperties;
import pe.gob.onpe.pradminbackend.security.properties.CifradoProperties;


@RestController
@Validated
@RequestMapping("/recurso")
@RequiredArgsConstructor
public class RecursosController {

    private final RecaptchaProperties recaptchaProperties;
    private final CifradoProperties cifradoProperties;

    @GetMapping(value="/apigoogle", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> apiGoogle() {
        Map<String, String> res = new HashMap<>();
        res.put("api", recaptchaProperties.siteKey());
        res.put("key", cifradoProperties.llavePublica());
        return res;
    }
}
