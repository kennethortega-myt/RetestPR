package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.repository.TabArchivoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.TabArchivoService;
import pe.gob.onpe.pradminbackend.model.dto.ArchivoTransmisionDto;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;
import pe.gob.onpe.pradminbackend.nfs.FileApp;
import pe.gob.onpe.pradminbackend.nfs.NfsService;
import pe.gob.onpe.pradminbackend.s3.S3Service;
import pe.gob.onpe.pradminbackend.utils.ArchivoUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TabArchivoServiceImpl implements TabArchivoService {

	private final TabArchivoRepository archivoRepository;
	private final NfsService nfsService;
	private final S3Service s3Service;
	
	@Override
	@Transactional
	public TabArchivo guardarImagenTransmitida(Long idActa, ArchivoTransmisionDto archivoDto, String path, Integer tipoArchivo) {
		TabArchivo archivo = new TabArchivo();
    	String formato = archivoDto.getExtension();
    	String mimetype = archivoDto.getMimeType();
    	String filename = archivoDto.getGuid();
        String filenameExt = filename+"."+formato;
    	String ruta = path+"/"+filenameExt;
        try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
            byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());
            archivo.setIdActa(idActa);
            archivo.setCGuid(archivoDto.getGuid());
            archivo.setCPeso(ArchivoUtils.formatBytes(decodedBytes.length));
            archivo.setCNombre(filename);
            archivo.setCNombreOriginal(filenameExt);
            archivo.setCRuta(ruta);
            archivo.setCFormato(mimetype);
            archivo.setAudFechaModificacion(new Date());
            archivo.setAudUsuarioModificacion("system");
            archivo.setTipo(tipoArchivo);
            fileOutputStream.write(decodedBytes);
            archivoRepository.save(archivo);
        } catch (Exception e) {
            return null;
        }
        return archivo;
		
	}
	
	@Override
	@Transactional
	public TabArchivo guardarArchivoTransmitidaWithException(Long idActa, ArchivoTransmisionDto archivoDto, String path, Integer tipoArchivo, boolean subirFtp) throws IOException {
		TabArchivo archivowe = new TabArchivo();
    	String formato = archivoDto.getExtension();
    	String mimetype = archivoDto.getMimeType();
    	String filename = archivoDto.getGuid();
        String filenameExt = filename+"."+formato;
    	String ruta = path+"/"+filenameExt;
    	byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());
    	if(subirFtp) {
    		FileApp fileApp = new FileApp(decodedBytes, filenameExt);
			this.nfsService.upload(fileApp);
			this.s3Service.uploadActa(fileApp);
    	} else {
    		try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
            	fileOutputStream.write(decodedBytes);
            } catch (Exception e) {
            	log.error("error en guardarArchivoTransmitidaWithException", e);
                throw new IOException("Error al guardar el archivo transmitido", e);
            }
    	}

        archivowe.setIdActa(idActa);
        archivowe.setCGuid(archivoDto.getGuid());
        archivowe.setCPeso(ArchivoUtils.formatBytes(decodedBytes.length));
        archivowe.setCNombre(filename);
        archivowe.setCNombreOriginal(filenameExt);
        archivowe.setCRuta(ruta);
        archivowe.setCFormato(mimetype);
        archivowe.setAudFechaCreacion(new Date());
        archivowe.setAudUsuarioCreacion("PR");
        archivowe.setActivo(1);
        archivowe.setTipo(tipoArchivo);
        archivoRepository.save(archivowe);
        return archivowe;
	}

	@Override
	@Transactional
	public TabArchivo actualizarArchivoTransmitidaWithException(Long idActa, ArchivoTransmisionDto archivoDto, String path, Integer tipoArchivo, TabArchivo archivoActualizar) throws IOException {

		String formato = archivoDto.getExtension();
		String mimetype = archivoDto.getMimeType();
		String filename = archivoDto.getGuid();
		String filenameExt = filename+"."+formato;
		String ruta = path+"/"+filenameExt;
		byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());

		FileApp fileApp = new FileApp(decodedBytes, filenameExt);
		this.nfsService.upload(fileApp);
		this.s3Service.uploadActa(fileApp);

		try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
			fileOutputStream.write(decodedBytes);
		} catch (Exception e) {
			log.error("error en actualizarArchivoTransmitidaWithException", e);
			throw new IOException("Error al actualizar el archivo transmitido", e);
		}

		archivoActualizar.setCGuid(archivoDto.getGuid());
		archivoActualizar.setCPeso(ArchivoUtils.formatBytes(decodedBytes.length));
		archivoActualizar.setCNombre(filename);
		archivoActualizar.setCNombreOriginalAnterior(archivoActualizar.getCNombreOriginal());
		archivoActualizar.setCNombreOriginal(filenameExt);
		archivoActualizar.setCRuta(ruta);
		archivoActualizar.setCFormato(mimetype);
		archivoActualizar.setAudFechaModificacion(new Date());
		archivoActualizar.setAudUsuarioModificacion("PR");
		archivoActualizar.setTipo(tipoArchivo);
		archivoRepository.save(archivoActualizar);
		return archivoActualizar;
	}

	@Override
	public Optional<TabArchivo> getArchivoById(String idActa) {
		return this.archivoRepository.findById(idActa);
	}
	
	@Override
	public EstadoServicioDto validarServicioFileServer() {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reportes/firmaOnpe.pdf")) {

			FileApp fileApp = new FileApp(inputStream.readAllBytes(), "firmaOnpe.pdf");
			this.nfsService.upload(fileApp);
			return EstadoServicioDto.builder()
	                .estado(true).build();
		} catch (Exception e) {
			return EstadoServicioDto.builder()
	                .estado(false)
	                .mensaje("No disponible: " + e.getMessage()).build();
		}
	}

}
