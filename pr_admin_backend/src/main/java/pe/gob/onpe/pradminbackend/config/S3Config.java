package pe.gob.onpe.pradminbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

	private final S3Properties properties;

	public S3Config(S3Properties properties) {
		this.properties = properties;
	}

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
				.region(Region.of(properties.getRegion()))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
	}

}
