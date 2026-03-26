package pe.gob.onpe.pradminbackend.model.bd.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface StorageService {

    void storeFile(MultipartFile file, String filename) throws IOException;

    Resource loadFile(String fileName) throws MalformedURLException;
    String loadFileAsBase64(String fileName) throws IOException;

    ByteArrayResource loadFileAsByteArrayResource(String fileName) throws IOException;


    String getPathUpload();

}
