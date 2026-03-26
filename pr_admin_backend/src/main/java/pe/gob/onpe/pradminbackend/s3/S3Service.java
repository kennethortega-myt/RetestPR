package pe.gob.onpe.pradminbackend.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.config.S3Properties;
import pe.gob.onpe.pradminbackend.nfs.FileApp;
import pe.gob.onpe.pradminbackend.utils.FtpUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties properties;

    public void uploadActa(FileApp file) {
        String fullKey = properties.getS3().getKeyActasPrefix() + "/" + file.getFilename();
        String contentType = resolveContentType(file.getFilename());
        uploadToS3(file, fullKey, contentType);
    }

    public void uploadToS3(FileApp file, String fullKey, String contentType) {
        try (InputStream fileStream = FtpUtils.convertToInputStream(file.getFile())) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(fullKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(fileStream, fileStream.available()));
            log.info("Archivo subido exitosamente a S3. Bucket: {}, fullKey: {}", properties.getBucketName(), fullKey);

        } catch (S3Exception e) {
            log.error("Error de S3 al subir archivo [{}]", fullKey, e);
            if (e.statusCode() == 401 || e.statusCode() == 403) {
                log.error("Credenciales AWS inválidas o expiradas", e);
            }
        } catch (SdkClientException e) {
            log.error("Error de conexión con AWS S3 [{}]", fullKey, e);
        } catch (IOException e) {
            log.error("Error al leer el archivo para subir a S3 [{}]", fullKey, e);
        } catch (Exception e) {
            log.error("Error inesperado al subir archivo a S3 [{}]", fullKey, e);
        }
    }

    public void copiarReporteDesdeEfsAS3(String nombreArchivo, String ruta) {
        Path pathEfs = Paths.get(
                ruta,
                nombreArchivo
        );

        if (Files.notExists(pathEfs)) {
            log.error("El archivo no existe en EFS {}", pathEfs);
            return;
        }

        try {
            String key = ruta.endsWith("/")
                    ? ruta + nombreArchivo
                    : ruta + "/" + nombreArchivo;


            s3Client.putObject(
                    software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                            .bucket(properties.getBucketName())
                            .key(key)
                            .build(),
                    pathEfs
            );

            log.info("Archivo {} copiado de EFS a S3 en {}",
                    nombreArchivo, key);

        } catch (Exception e) {
            log.error("Error al copiar archivo de EFS a S3", e);
        }
    }

    public void limpiarBucket() {

        String bucket = properties.getBucketName();
        String continuationToken = null;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {

            do {

                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .maxKeys(1000)
                        .continuationToken(continuationToken)
                        .build();

                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

                List<ObjectIdentifier> objectsToDelete = new ArrayList<>();

                for (S3Object s3Object : listResponse.contents()) {
                    objectsToDelete.add(
                            ObjectIdentifier.builder()
                                    .key(s3Object.key())
                                    .build()
                    );
                }

                if (!objectsToDelete.isEmpty()) {

                    executor.submit(() -> {
                        try {

                            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                                    .bucket(bucket)
                                    .delete(Delete.builder().objects(objectsToDelete).build())
                                    .build();

                            s3Client.deleteObjects(deleteRequest);

                            log.info("Eliminados {} archivos", objectsToDelete.size());

                        } catch (Exception e) {
                            log.error("Error eliminando batch de archivos", e);
                        }
                    });
                }

                continuationToken = listResponse.nextContinuationToken();

            } while (continuationToken != null);

        } catch (Exception e) {
            log.error("Error listando objetos del bucket", e);

        } finally {

            executor.shutdown();

            try {
                if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

        }

        log.info("Limpieza completa del bucket {}", bucket);
    }


    private String resolveContentType(String fileName) {
        try {
            return Files.probeContentType(Path.of(fileName));
        } catch (Exception e) {
            return null;
        }
    }

}
