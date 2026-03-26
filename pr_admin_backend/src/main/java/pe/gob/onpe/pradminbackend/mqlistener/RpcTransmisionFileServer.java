package pe.gob.onpe.pradminbackend.mqlistener;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import pe.gob.onpe.pradminbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.repository.TabArchivoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.TabArchivoService;
import pe.gob.onpe.pradminbackend.model.dto.ArchivoTransmisionDto;
import pe.gob.onpe.pradminbackend.model.dto.ArchivoTransmitidoDto;
import pe.gob.onpe.pradminbackend.model.dto.request.ArchivoTransmisionRequest;
import pe.gob.onpe.pradminbackend.model.dto.response.ArchivoTransmisionResponse;
import pe.gob.onpe.pradminbackend.utils.ConstantesComunes;

@Service
@Slf4j
public class RpcTransmisionFileServer {

	@Autowired
	private TabArchivoService tabArchivoService;

	@Autowired
	private TabArchivoRepository tabArchivoRepository;

	@Value("${pr.nfs.path}")
	private String pathNfs;

	@Value("${pr.nfs.files}")
	private String pathFiles;
	
	@RabbitListener(queues = "${app.sce-batch-pr.transmision.send-queue-file}")
    @SendTo // Responde a la cola reply-to especificada en las propiedades del mensaje
    public String receive(String message) throws JsonProcessingException {
		
		ArchivoTransmisionRequest archivoDto = ArchivoTransmisionRequest.getObject(message);

		Long idActa = archivoDto.getIdActa();

		List<ArchivoTransmisionDto> archivos = archivoDto.getArchivos();

		List<ArchivoTransmitidoDto> archivosGuardados = null;
		
		if(archivos!=null) {
			archivosGuardados = archivos.stream()
					.map(archivo -> {
						try {
							return getArchivoTransmitido(idActa, archivo, pathNfs.concat(pathFiles), archivo.getTipoArchivo());
						} catch (Exception e) {
							return ArchivoTransmitidoDto.builder()
									.guid(archivo.getGuid())
									.transmitido(false)
									.build();
						}
					})
					.toList();
		}
		
		
		// prepare the answer
    	ArchivoTransmisionResponse response = ArchivoTransmisionResponse.builder()
    			.archivos(archivosGuardados)
    			.build();
    	

    	return response.toJson();
    }
	
	public ArchivoTransmitidoDto getArchivoTransmitido(Long idActa, ArchivoTransmisionDto archivoTransmitidoDto, String path, Integer tipoArchivo) throws Exception {
		ArchivoTransmitidoDto dto = null;
		if(archivoTransmitidoDto!=null) {
			Optional<TabArchivo> archivoBD;
			TabArchivo archivo;
			dto = new ArchivoTransmitidoDto();
			if (tipoArchivo != 5 ) { //actas de 1 al 4
				archivoBD =  tabArchivoRepository.findByTipoAndIdActa(tipoArchivo,idActa);
			} else { // resoluciones 5
				archivoBD =  tabArchivoRepository.findBycGuidAndTipoAndIdActa(archivoTransmitidoDto.getGuid(), tipoArchivo,idActa);
			}
			if(archivoBD.isPresent()){
				archivo = archivoBD.get();
				if (tipoArchivo != 5 ) { // es acta se actualiza
					log.error("El archivo transmitido es acta ya existe en BD PR, se actualiza");
					tabArchivoService.actualizarArchivoTransmitidaWithException(idActa, archivoTransmitidoDto, path, tipoArchivo, archivo);
				} else {
					log.error("El archivo transmitido es Resolucion ya existe en BD PR, No se registra");
				}

			} else {
				archivo = tabArchivoService.guardarArchivoTransmitidaWithException(idActa, archivoTransmitidoDto, path, tipoArchivo, true);
			}
			dto.setGuid(archivo.getCGuid());
			dto.setTransmitido(archivo.getCGuid() != null);
		} 
		return dto;
	}
	
}
