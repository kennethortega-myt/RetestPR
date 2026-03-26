
package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.service.*;
import pe.gob.onpe.pradminbackend.model.dto.bd.importar.*;
import pe.gob.onpe.pradminbackend.model.dto.mapper.UtilMapper;
import pe.gob.onpe.pradminbackend.model.dto.request.ImportarPaginadoRequest;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportarBdPaginadoAsyncServiceImpl {
	
    private final RestTemplate clientExport;
	
	@Value("${sce.nacion.url}")
    private String urlNacion;

    public static final String BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS = "/topic/update-progress-with-details";
    
    private final BroadcastWebsocketServiceImpl broadcastWebsocketService;
    private final ObjectMapper objectMapper;
    private final MaePadronService maePadronService;
    private final MaePadronProgresoService maePadronProgresoService;

	private void reportProgressImportarPaginado(float percent, String message, String estado) {
        Map<String, Object> payloadImpPaginado = new HashMap<>();
        payloadImpPaginado.put("porcentaje", percent);
        payloadImpPaginado.put("texto", message);
        payloadImpPaginado.put("estado", estado);
        try {
            this.broadcastWebsocketService.broadcastProgressUpdate(BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS, this.objectMapper.writeValueAsString(payloadImpPaginado));
            Thread.sleep(100);
        } catch (InterruptedException | JsonProcessingException e) {
            log.error("Error en reportProgressImportarPaginado: ", e);
            Thread.currentThread().interrupt();
        }
    }

    private int total = 1;
    private int current = 0;
    private float getPercent() {
    	float porcentaje = (this.current++) * 100 / (float) this.total;
    	log.info("Avance del porcentaje: " + porcentaje);
        return Float.parseFloat(String.format("%.2f", porcentaje));
    }

    @Async
    public CompletableFuture<Void> migrar(String proceso) {
    	this.total=1;
    	this.current=0;
    	MaePadronProgreso maePadronProgreso = this.maePadronProgresoService.ultimoRegistro();
    	
    	HttpHeaders headers = new HttpHeaders();
        headers.set(PrConstantes.USERAGENT_HEADER, PrConstantes.USERAGENT_HEADER_VALUE);
        headers.set(PrConstantes.TENANT_HEADER,  proceso);
        headers.set("Content-Type", "application/json");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<ImportarPaginadoRequest> httpEntity = new HttpEntity<>(headers);
        
        ResponseEntity<ImportarPaginadoDto> response;
        String url = urlNacion + "padron-electoral/exportacion/pr";
        
        Integer nPagina = 0;
        Integer tPagina = 10000;
        Boolean nextRow=true;
        if(maePadronProgreso!=null && maePadronProgreso.getSiguiente()) {
        	nPagina = maePadronProgreso.getPagina();
        	nextRow=true;
        } else if (maePadronProgreso!=null && !maePadronProgreso.getSiguiente()) {
        	nextRow=false;
        }
        
        if(Boolean.TRUE.equals(nextRow)) {
	        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);
	    	urlBuilder.replaceQueryParam("tamanoPagina", tPagina);
	        do {
	        	log.info("num pagina" +nPagina);
	        	urlBuilder.replaceQueryParam("numeroPagina", nPagina);
	        	response = this.clientExport.exchange(urlBuilder.toUriString(), HttpMethod.GET, httpEntity, ImportarPaginadoDto.class);
	        	Long num = response.getBody().getTotalPaginas();
	        	this.current=nPagina;
	            this.total = num.intValue();
	        	this.reportProgressImportarPaginado(getPercent(), "Se inició la migración de procesos electorales, item "+ (nPagina+1) + "/"+total, PrConstantes.ESTADO_PROGRESO_CONTINUA);
	            List<MaePadron> list = response.getBody().getData().parallelStream()
	                    .map(UtilMapper::convertirPadron)
	                    .toList();
	            this.maePadronService.saveAll(list);
	            if(maePadronProgreso!=null) {
	            	maePadronProgreso.setPagina(nPagina);
	            	maePadronProgreso.setSiguiente(response.getBody().isNext());		
	            	maePadronProgreso.setAudUsuarioCreacion("USER_PR");
	            	maePadronProgreso.setAudFechaCreacion(new Date());
	            	this.maePadronProgresoService.save(maePadronProgreso);
	            } else {
	            	MaePadronProgreso maePadronProgreso1 = MaePadronProgreso.builder()
	            			.pagina(nPagina)
	            			.siguiente(response.getBody().isNext())
	            			.audUsuarioCreacion("USER_PR")
	            			.audFechaCreacion(new Date())
	            			.build();
	            	this.maePadronProgresoService.save(maePadronProgreso1);
	            }
	            log.info("Se ejecutó el método para insertar padrón electoral" + PrConstantes.ESTADO_PROGRESO_CONTINUA);
	            ++nPagina;
			} while (response.getBody().isNext());
        	this.reportProgressImportarPaginado(getPercent(), "Finalizó la carga de datos", PrConstantes.ESTADO_PROGRESO_FINALIZA_OK);
        } else {
        	this.reportProgressImportarPaginado(getPercent(), "La carga de datos está completa.", PrConstantes.ESTADO_PROGRESO_FINALIZA_OK);
        }
        return CompletableFuture.completedFuture(null);
    }
}

