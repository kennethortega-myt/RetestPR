package pe.gob.onpe.consultaopbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

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

	@Bean
	public S3AsyncClient s3AsyncClient() {
		return S3AsyncClient.builder()
				.region(Region.of(properties.getRegion()))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
	}

	@Bean
	public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
		return S3TransferManager.builder()
				.s3Client(s3AsyncClient)
				.build();
	}

}
