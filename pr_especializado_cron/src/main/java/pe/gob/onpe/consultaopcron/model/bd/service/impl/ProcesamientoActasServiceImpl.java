package pe.gob.onpe.consultaopcron.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.consultaopcron.model.bd.service.ProcesamientoActasService;
import pe.gob.onpe.consultaopcron.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopcron.security.jwt.JwtTokenFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@Service
@Slf4j
public class ProcesamientoActasServiceImpl implements ProcesamientoActasService {

    private final RestTemplate restTemplate;
    private final JwtTokenFactory jwtTokenFactory;

    @Value("${cop.backend.url}")
    private String urlBackend;

    public ProcesamientoActasServiceImpl(@Qualifier("servicioExterno") RestTemplate restTemplate,
            JwtTokenFactory jwtTokenFactory) {
        this.restTemplate = restTemplate;
        this.jwtTokenFactory = jwtTokenFactory;
    }

    @Override
    public void enviarPeticionProcesarActas(String idConfiguracion) {

        try {

            String url = urlBackend + "/procesamientoActas/procesar/{idConfiguracion}";
            String jwtToken = jwtTokenFactory.createServiceToServerToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String finalUrl = url.replace("{idConfiguracion}", idConfiguracion);
            log.info("cURL: curl -X GET -H \"Authorization: Bearer {}\" {}", jwtToken, finalUrl);

            ResponseEntity<ReporteCronResponse> responseService = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReporteCronResponse.class,
                    idConfiguracion);

            if (responseService.getStatusCode() == HttpStatus.OK) {
                log.info("Procesamiento de actas exitoso para ID: {}", idConfiguracion);
            } else {
                log.error("Ocurrió un error, verificar response: {}", responseService);
            }

        } catch (HttpClientErrorException e) {
            log.error("Error de cliente al procesar actas: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Error de servidor al procesar actas: {}", e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado al procesar actas", e);
        }
    }
}
