package pe.gob.onpe.consultaopbackend.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.config.S3Properties;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties properties;

    private final S3TransferManager transferManager;

    public void copiarReporteDesdeEfsAS3(String nombreArchivo, String ruta) {
        Path pathEfs = Paths.get(ruta)
                .resolve(nombreArchivo);

        if (Files.notExists(pathEfs)) {
            log.error("El archivo no existe en EFS {}", pathEfs);
            return;
        }

        try {
            String key = ruta.endsWith("/")
                    ? ruta + nombreArchivo
                    : ruta + "/" + nombreArchivo;

            String fullKey = properties.getS3().getKeyReportesPrefix() + key;

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getBucketName())
                            .key(fullKey)
                            .build(),
                    pathEfs);

            log.info("Archivo {} copiado de EFS a S3 en {}",
                    nombreArchivo, key);

        } catch (Exception e) {
            log.error("Error al copiar archivo de EFS a S3", e);
        }
    }

    public List<String> listarObjetosBucketActas() {
        log.info("bucket actas name: {}", properties.getBucketActasName());
        log.info("aws prefix actas: {}", properties.getS3().getKeyActasPrefix());
        List<String> keys = new ArrayList<>();
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(properties.getBucketActasName())
                .prefix(properties.getS3().getKeyActasPrefix())
                .build();

        ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);
        for (ListObjectsV2Response response : responses) {
            response.contents().forEach(s3Object -> keys.add(s3Object.key()));
        }
        log.info("Encontrados {} objetos en s3://{}/{}", keys.size(), properties.getBucketActasName(), properties.getS3().getKeyActasPrefix());
        return keys;
    }

    public void descargarObjetoBucketActas(String key, String rutaDestino) throws IOException {
        Path destino = Paths.get(rutaDestino);

        Files.createDirectories(destino.getParent());

        DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                .getObjectRequest(b -> b.bucket(properties.getBucketActasName()).key(key))
                .destination(destino)
                .build();

        FileDownload download = transferManager.downloadFile(downloadFileRequest);
        CompletedFileDownload completed = download.completionFuture().join();
        log.info("Descargado: s3://{}/{} -> {}", properties.getBucketActasName(), key, rutaDestino);
    }

    public void subirArchivoBucketReporte(String key, File archivo) {
        if (!archivo.exists()) {
            throw new IllegalArgumentException("El archivo no existe: " + archivo.getAbsolutePath());
        }

        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(b -> b.bucket(properties.getBucketName()).key(properties.getS3().getKeyReportesPrefix() + "/" + key))
                .source(archivo.toPath())
                .build();

        var upload = transferManager.uploadFile(uploadFileRequest);
        upload.completionFuture().join();
        log.info("Subido: {} -> s3://{}/{}", archivo.getAbsolutePath(), properties.getBucketName(), key);
    }

    public List<String> listarObjetosBucketReporte() {
        String basePrefix = properties.getS3().getKeyReportesPrefix();
        // Si no termina con '/', se lo añadimos para construir correctamente
        if (!basePrefix.endsWith("/")) {
            basePrefix = basePrefix + "/";
        }

        String fullPrefix = basePrefix;

        List<String> keys = new ArrayList<>();
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(properties.getBucketName())
                    .prefix(fullPrefix)
                    .build();

            ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);
            for (ListObjectsV2Response response : responses) {
                response.contents().forEach(s3Object -> keys.add(s3Object.key()));
            }
            log.info("Encontrados {} objetos en s3://{}/{}", keys.size(), properties.getBucketName(), fullPrefix);
        } catch (Exception e) {
            log.error("Error al listar objetos en bucket de reportes con prefijo {}: {}", fullPrefix, e.getMessage(), e);
        }
        return keys;
    }

    public long obtenerTamanioObjeto(String key) {
        try {
            var head = s3Client.headObject(b -> b.bucket(properties.getBucketName()).key(key));
            return head.contentLength();
        } catch (Exception e) {
            log.error("Error al obtener tamaño del objeto {}: {}", key, e.getMessage());
            return 0L;
        }
    }

    public Date obtenerFechaModificacion(String key) {
        try {
            var head = s3Client.headObject(b -> b.bucket(properties.getBucketName()).key(key));
            return Date.from(head.lastModified());
        } catch (Exception e) {
            log.error("Error al obtener fecha de modificación del objeto {}: {}", key, e.getMessage());
            return null;
        }
    }

}
