package pe.gob.onpe.consultaopbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "pe.gob.onpe.consultaopbackend.model.bd.repository.secondary", mongoTemplateRef = "secondaryMongoTemplate")
public class MongoSecondaryConfig {
	
	@Bean(name = "secondaryMongoProperties")
	@ConfigurationProperties(prefix = "spring.data.mongodb.secondary")
	public MongoProperties getSecondaryProperties() {
        return new MongoProperties();
    }
	
	@Bean(name = "secondaryMongoTemplate")
	public MongoTemplate secondaryMongoTemplate() throws Exception {
        return new MongoTemplate(secondaryMongoClient(), getSecondaryProperties().getDatabase());
    }
	
	@Bean(name = "secondaryMongoClient")
	public MongoClient secondaryMongoClient() {
		return MongoClients.create(getSecondaryProperties().getUri());
	}
}
