package pe.gob.onpe.pradminbackend.nfs;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pe.gob.onpe.pradminbackend.exception.SaveFileException;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.model.dto.response.ResponseEliminarDescargaActas;
import pe.gob.onpe.pradminbackend.utils.FtpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Log4j2
@Service
public class NfsService {

    @Value("${pr.nfs.path}")
    private String pathNfs;

    @Value("${pr.nfs.files}")
    private String pathFiles;

    @Value("${cop.backend.url}")
    private String urlCopBackend;

    @Value("${boletines.backend.url}")
    private String urlBoletinesBackend;

    @Value("${admin-nube-backend-url}")
    private String adminNubeBackendUrl;

    @Value("${pr_admin_access_key}")
    private String adminAccessKey;

    @Autowired
    private RestTemplate restTemplate;

    public FileApp download(String filename) throws IOException {

        String pathFile = getPathFile(filename);
        try (InputStream is = new FileInputStream(pathFile)) {
            byte[] fileBytes = FtpUtils.convertToByte(is);
            return new FileApp(fileBytes, filename);
        } catch (IOException e) {
            log.error("Error al leer archivo del NFS", e);
            throw new IOException("Error al leer archivo del NFS", e);
        }
    }

    public void upload(FileApp file, String path) {
        this.createDirectory();
        try (InputStream fileStream = FtpUtils.convertToInputStream(file.getFile())) {
            Path pathFull = Paths.get(path);
            FileUtils.copyInputStreamToFile(fileStream,
                    new File(pathFull.resolve(file.getFilename()).toUri()));
            log.info("URI: " + pathFull.resolve(file.getFilename()).toUri());
        } catch (IOException e) {
            log.error("Error al subir archivo al NFS: ", e);
            throw new SaveFileException("Error al subir el archivo al NFS");
        }
    }

    public void upload(FileApp file) {

        log.info("Subiendo archivo: {}, Tamaño: {} bytes", file.getFilename(), file.getSize());
        this.createDirectory();
        try (InputStream fileStream = FtpUtils.convertToInputStream(file.getFile())) {
            Path pathFull = Paths.get(getPathNfsFull());
            FileUtils.copyInputStreamToFile(fileStream,
                    new File(pathFull.resolve(file.getFilename()).toUri()));
            log.info("URI: " + pathFull.resolve(file.getFilename()).toUri());
        } catch (IOException e) {
            log.error("Error al subir archivo al NFS: ", e);
            throw new SaveFileException("Error al subir el archivo al NFS");
        }

    }

    private void createDirectory() {

            Path path = Paths.get(pathNfs.concat(pathFiles));
            log.info("path: " + path);
            try {
                if (path.getNameCount() >= 1 && Files.notExists(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                log.error("Error al crear directorio: ",e);
            }

    }

    private String getPathFile(String fileName) {

        if (pathFiles.endsWith("/") || pathFiles.endsWith("\\")) {
            return pathNfs.concat(pathFiles).concat(fileName);
        } else {
            return pathNfs.concat(pathFiles).concat("/").concat(fileName);
        }
    }

    private String getPathNfsFull() {
        if (pathFiles.endsWith("/") || pathFiles.endsWith("\\")) {
            String pathFull = pathNfs.concat(pathFiles);
            return pathFull.substring(0, pathFull.length() - 1);
        } else {
            return pathNfs.concat(pathFiles);
        }

    }

    public void eliminarTodosLosArchivos() {
        Path directorio = Paths.get(getPathNfsFull());
        log.info("directorio: " + directorio);
        if (Files.notExists(directorio)) {
            log.info("directorio no existe, nada que eliminar");
            return;
        }
        try (Stream<Path> archivos = Files.walk(directorio)) {
            archivos
                    .filter(path -> !path.equals(directorio))
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            log.info("Eliminado: " + path.getFileName());
                        } catch (IOException e) {
                            log.error("Error al eliminar: " + path.getFileName() + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.error("Error al listar archivos: " + e.getMessage());
        }
    }

    /**
     * Elimina completamente la carpeta descarga-actas
     * Ruta: cop.nfs.path + cop.nfs.files
     */
    public Boolean eliminarCarpetaDescargaActas() {
        boolean eliminado = false;

        try {
            String url = urlCopBackend + "/procesamientoActas/eliminarCarpetaDescargaActas";

            ResponseEntity<ResponseEliminarDescargaActas> responseService = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    ResponseEliminarDescargaActas.class
            );

            if (responseService.getStatusCode() == HttpStatus.OK) {
                eliminado = true;
                log.info(" Response del backend COP: " + responseService.getBody().getMensaje());

            } else {
                eliminado = false;
                log.error(" Ocurrió un error, verificar response: " + responseService.getBody().getMensaje());
            }
        } catch (HttpClientErrorException e) {
            log.error("   Error de cliente al borrar descarga-actas: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("   Error de servidor al borrar descarga-actas: {}", e.getMessage());

        } catch (Exception e) {
            log.error("   Error inesperado al borrar descarga-actas: {}", e.getMessage());
        }

        return eliminado;
    }

    public boolean eliminarBoletines() {
        log.info("Ingresando puestaACero");
        try {
            String url = urlBoletinesBackend + "/proceso/puestacero";
            ResponseEntity<GenericResponse> responseService = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    GenericResponse.class
            );

            if (responseService.getStatusCode() == HttpStatus.OK &&
                responseService.getBody() != null &&
                responseService.getBody().isSuccess()) {
                log.info("Puesta a cero exitosa: {}", responseService.getBody().getData());
                return true;
            } else {
                String errorMsg = responseService.getBody() != null ? responseService.getBody().getMessage() : "No body";
                log.error("Ocurrió un error en puestaACero. Status: {}, Body message: {}", responseService.getStatusCode(), errorMsg);
                return false;
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error HTTP al ejecutar puesta a cero: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error inesperado al ejecutar puesta a cero: {}", e.getMessage());
            return false;
        }
    }

    public void puestaCeroNube() {
        try {
            String url = UriComponentsBuilder.fromUriString(adminNubeBackendUrl + "/trama-sce/puesta-cero")
                    .queryParam("usuarioSce", "pradmin")
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminAccessKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Puesta a cero en nube realizada correctamente");

            } else {
                log.error("Ocurrió un error al realizar la puesta a cero en nube");
            }
        } catch (HttpClientErrorException e) {
            log.error("Error de cliente al borrar descarga-actas: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Error de servidor al borrar descarga-actas: {}", e.getMessage());

        } catch (Exception e) {
            log.error("   Error inesperado al borrar descarga-actas: {}", e.getMessage());
        }
    }
}
