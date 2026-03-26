package pe.gob.onpe.consultaopbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "pe.gob.onpe.consultaopbackend.model.bd.repository.primary", mongoTemplateRef = "primaryMongoTemplate")
public class MongoPrimaryConfig {
	
	@Primary
    @Bean(name = "primaryMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    public MongoProperties getPrimaryProperties() {
        return new MongoProperties();
    }
	
	@Primary
	@Bean(name = "primaryMongoTemplate")
	public MongoTemplate primaryMongoTemplate() throws Exception {
		return new MongoTemplate(primaryMongoClient(), getPrimaryProperties().getDatabase());
	}
	
	@Primary
	@Bean(name = "primaryMongoClient")
	public MongoClient primaryMongoClient() {
		return MongoClients.create(getPrimaryProperties().getUri());
	}
}
