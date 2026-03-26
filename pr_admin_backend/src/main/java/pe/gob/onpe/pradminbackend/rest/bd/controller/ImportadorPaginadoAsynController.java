
package pe.gob.onpe.pradminbackend.rest.bd.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.service.impl.ImportarBdPaginadoAsyncServiceImpl;
import pe.gob.onpe.pradminbackend.security.TokenDecoder;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;

@Controller
@Slf4j
public class ImportadorPaginadoAsynController {

    @Autowired
    @Qualifier("servicioMigracion")
    private RestTemplate clientExport;

    @Autowired
    private ImportarBdPaginadoAsyncServiceImpl importar;

    @Value("${sce.nacion.url}")
    private String urlNacion;
    
    @Autowired
	private TokenDecoder tokenDecoder;

    @MessageMapping("/importar-paginado-ws") // para enviar mensaje al servidor
    public String recibirMensaje(SimpMessageHeaderAccessor headerAccessor) {
    	
		List<String> values = headerAccessor.getNativeHeader(PrConstantes.AUTHORIZATION_HEADER);
		
		String proceso = null;
		
		if(values!=null) {
        	String authorizationHeader = values.get(0);
        	String token = authorizationHeader.substring(PrConstantes.LENGTH_BEARER); // remove prefix "Bearer "
            Claims claims = this.tokenDecoder.decodeToken(token);
            proceso = claims.get("apr", String.class);
        } // end
    	
        log.info("****************************");
        log.info("Proceso: " + proceso);
        log.info("****************************");
        this.importar.migrar(proceso);
        
        return "";
    }
}

