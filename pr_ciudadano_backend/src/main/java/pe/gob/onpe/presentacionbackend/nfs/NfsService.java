package pe.gob.onpe.presentacionbackend.nfs;


import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.presentacionbackend.exception.NotFoundException;
import pe.gob.onpe.presentacionbackend.utils.FtpUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
@Service
public class NfsService {

    @Value("${pr.nfs.path}")
    private String pathNfs;

    @Value("${pr.nfs.files}")
    private String pathFiles;


    public FileApp download(String filename) throws IOException {

        String pathFile = getPathFile(filename);
        try (InputStream is = new FileInputStream(pathFile)) {
            byte[] fileBytes = FtpUtils.convertToByte(is);
            return new FileApp(fileBytes, filename);
        } catch (IOException e) {
            log.error("Error al leer archivo del NFS", e);
            throw new NotFoundException("Error al leer archivo del NFS " + e.getMessage());
        }
    }


    private String getPathFile(String fileName) {

        if (pathFiles.endsWith("/") || pathFiles.endsWith("\\")) {
            return pathNfs.concat(pathFiles).concat(fileName);
        } else {
            return pathNfs.concat(pathFiles).concat("/").concat(fileName);
        }
    }

}
