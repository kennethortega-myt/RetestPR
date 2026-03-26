package pe.gob.onpe.pradminbackend.rest.bd.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.service.impl.ImportarBdAsyncServiceImpl;
import pe.gob.onpe.pradminbackend.model.dto.bd.importar.ImportarDto;
import pe.gob.onpe.pradminbackend.model.dto.request.ImportarRequest;
import pe.gob.onpe.pradminbackend.security.TokenDecoder;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;

@Controller
@Slf4j
public class ImportadorAsynController {

    @Autowired
    @Qualifier("servicioMigracion")
    private RestTemplate clientExport;
    
    @Autowired
    private ImportarBdAsyncServiceImpl importar;

   @Value("${sce.nacion.url}")
    private String urlNacion;
    
    @Autowired
	private TokenDecoder tokenDecoder;

    @MessageMapping("/importar-ws") // para enviar mensaje al servidor
    public String recibirMensaje(@Payload ImportarRequest request, SimpMessageHeaderAccessor headerAccessor) throws Exception {        
        boolean requiereVistaEleccion = Integer.valueOf(1).equals(request.getGetVistaEleccion());
        boolean bdValida = this.importar.isBdValida();

        if (requiereVistaEleccion && !bdValida) {
            this.importar.reportProgressImportar(
                this.importar.getPercent(false),
                "Primero debe migrar la opción Obtener BD",
                PrConstantes.ESTADO_PROGRESO_FINALIZA_ERROR
            );
            return "";
        }
        
		List<String> values = headerAccessor.getNativeHeader(PrConstantes.AUTHORIZATION_HEADER);
		
		String proceso = null;
		
		if(values!=null) {
        	String authorizationHeader = values.get(0);
        	String token = authorizationHeader.substring(PrConstantes.LENGTH_BEARER); // remove prefix "Bearer "
            Claims claims = this.tokenDecoder.decodeToken(token);
            proceso = claims.get("apr", String.class); //"EG2026_2";//EG2026PR //EG2026 //
        } // end
		
		request.setAcronimoProceso(proceso);
        log.info("*****************************");
        log.info("Proceso: "+ proceso);
        log.info("*****************************");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(PrConstantes.USERAGENT_HEADER, PrConstantes.USERAGENT_HEADER_VALUE);
        headers.set(PrConstantes.TENANT_HEADER,  proceso);
        headers.set("Content-Type", "application/json");
        HttpEntity<ImportarRequest> httpEntity = new HttpEntity<>(request, headers);
        
        log.info("****************************");
        log.info("Inicio de la exportacion");
        log.info("****************************");
        ResponseEntity<ImportarDto> response = null;
        try{
           response = this.clientExport.exchange(urlNacion + "exportar-pr/", HttpMethod.POST, httpEntity, ImportarDto.class);
        }catch(Exception e) {
            log.error("error SCE method: exportar-pr = {}", e.getMessage());
            log.error("",e);
        }
        log.info("***************************");
        log.info("Fin de la exportacion");
        log.info("***************************");

        this.importar.migrar( response == null ? null:response.getBody(), request);
        
        return "";
    }
}

