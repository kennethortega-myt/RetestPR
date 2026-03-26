package pe.gob.onpe.consultaopbackend.nfs;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.exception.DownloadActaException;
import pe.gob.onpe.consultaopbackend.exception.SaveFileException;
import pe.gob.onpe.consultaopbackend.utils.FtpUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@Service
public class NfsService {

    @Value("${cop.nfs.path}")
    private String pathNfs;

    @Value("${cop.nfs.files}")
    private String pathFiles;

    @Value("${pr.nfs.actas.origen}")
    private String pathActasFiles;

    public FileApp download(String filename) throws IOException {

        String pathFile = getPathFile(filename);
        try (InputStream is = new FileInputStream(pathFile)) {
            byte[] fileBytes = FtpUtils.convertToByte(is);
            return new FileApp(fileBytes, filename);
        } catch (FileNotFoundException e) {
            log.error("No se encontro el archivo.", e);
            throw new DownloadActaException("No se encontro el archivo.");
        } catch (IOException e) {
            log.error("Error al leer archivo del NFS", e);
            throw new DownloadActaException("Error al leer archivo del NFS");
        }
    }

    public FileApp downloadActa(String filename) throws IOException {

        String pathFile = getPathActaFile(filename);
        try (InputStream is = new FileInputStream(pathFile)) {
            byte[] fileBytes = FtpUtils.convertToByte(is);
            return new FileApp(fileBytes, filename);
        } catch (FileNotFoundException e) {
            log.error("No se encontro el archivo.", e);
            throw new DownloadActaException("No se encontro el archivo.");
        } catch (IOException e) {
            log.error("Error al leer archivo del NFS", e);
            throw new DownloadActaException("Error al leer archivo del NFS");
        }
    }

    public void upload(FileApp file, String path) {
        this.createDirectory(path);
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

    public void upload(FileApp file) throws Exception {

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

    private void createDirectory(String customPath) {
        Path path = Paths.get(customPath);
        log.info("path: " + path);
        try {
            if (path.getNameCount() >= 1 && Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("Error al crear directorio: ", e);
        }
    }

    private void createDirectory() {

        Path path = Paths.get(pathNfs.concat(pathFiles).concat(pathNfs));
        log.info("path: " + path);
        try {
            if (path.getNameCount() >= 1 && Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("Error al crear directorio: ", e);
        }

    }

    private String getPathFile(String fileName) {

        if (pathFiles.endsWith("/") || pathFiles.endsWith("\\")) {
            return pathNfs.concat(pathFiles).concat(pathNfs).concat(fileName);
        } else {
            return pathNfs.concat(pathFiles).concat(pathNfs).concat("/").concat(fileName);
        }
    }

    private String getPathNfsFull() {
        if (pathFiles.endsWith("/") || pathFiles.endsWith("\\")) {
            String pathFull = pathNfs.concat(pathFiles).concat(pathNfs);
            return pathFull.substring(0, pathFull.length() - 1);
        } else {
            return pathNfs.concat(pathFiles).concat(pathNfs);
        }

    }

    private String getPathActaFile(String fileName) {

        if (pathActasFiles.endsWith("/") || pathActasFiles.endsWith("\\")) {
            return pathActasFiles.concat(fileName);
        } else {
            return pathActasFiles.concat("/").concat(fileName);
        }
    }
}
