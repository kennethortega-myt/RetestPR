package pe.gob.onpe.pradminbackend.model.bd.service;

import java.io.IOException;
import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.pradminbackend.model.dto.ArchivoTransmisionDto;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;

public interface TabArchivoService {

	TabArchivo guardarImagenTransmitida(Long idActa, ArchivoTransmisionDto archivoTransmitidoDto, String path, Integer tipoArchivo);
	
	TabArchivo guardarArchivoTransmitidaWithException(Long idActa, ArchivoTransmisionDto archivoTransmitidoDto, String path, Integer tipoArchivo, boolean subirFtp) throws IOException;
	TabArchivo actualizarArchivoTransmitidaWithException(Long idActa, ArchivoTransmisionDto archivoDto, String path, Integer tipoArchivo, TabArchivo archivoActualizar) throws IOException;
	Optional<TabArchivo> getArchivoById(String idActa);
	
	EstadoServicioDto validarServicioFileServer();
	
}
