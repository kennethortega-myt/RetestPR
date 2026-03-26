package pe.gob.onpe.consultaopcron.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.consultaopcron.model.bd.service.ProcesarReporteService;
import pe.gob.onpe.consultaopcron.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopcron.security.jwt.JwtTokenFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@Service
@Slf4j
public class ProcesarReporteServiceImpl implements ProcesarReporteService {

    private final RestTemplate restTemplate;
    private final JwtTokenFactory jwtTokenFactory;

    @Value("${cop.backend.url}")
    private String urlServicioProcesarReporte;

    public ProcesarReporteServiceImpl(@Qualifier("servicioExterno") RestTemplate restTemplate,
            JwtTokenFactory jwtTokenFactory) {
        this.restTemplate = restTemplate;
        this.jwtTokenFactory = jwtTokenFactory;
    }

    @Override
    public void enviarPeticionProcesarReporte(String idReporte) {

        try {
            String url = urlServicioProcesarReporte + "/reporteProgramado/procesar/{idReporte}";
            String jwtToken = jwtTokenFactory.createServiceToServerToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ReporteCronResponse> responseService = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReporteCronResponse.class,
                    idReporte);

            if (responseService.getStatusCode() == HttpStatus.OK) {
                log.info("Peticion enviada, reporte: {} , respuesta: {}", idReporte,
                        responseService.getBody().getResponse().getMessage());

            } else if (responseService.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("servicio retorna 404 Not Found.");
            } else if (responseService.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.info("servicio retorna 500 Internal Server Error.");
                log.error("erro del servicio generar reporte : {}", responseService.getBody());
            } else {
                log.info("servicio retorna un codigo de error no mapeado");
                log.error("erro no mapeado de servicio generar reporte : {}", responseService);
            }
        } catch (HttpClientErrorException e) {
            log.error("Error de cliente: {} - {}", e.getStatusCode(), e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Error de servidor {} - {}", e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado", e);
        }

    }
}
