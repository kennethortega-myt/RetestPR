package pe.gob.onpe.consultaopbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aws")
public class S3Properties {

    private String region;
    private String bucketName;
    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private String bucketActasName;

    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String keyReportesPrefix;
        private String keyActasPrefix;
    }
}
