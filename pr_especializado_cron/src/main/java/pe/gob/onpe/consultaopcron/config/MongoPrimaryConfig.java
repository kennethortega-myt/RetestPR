package pe.gob.onpe.consultaopcron.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "pe.gob.onpe.consultaopcron.model.bd.repository.primary", mongoTemplateRef = "primaryMongoTemplate")
public class MongoPrimaryConfig {
	
	@Primary
    @Bean(name = "primaryMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    public MongoProperties getPrimaryProperties() {
        return new MongoProperties();
    }
	
	@Primary
	@Bean(name = "primaryMongoTemplate")
	public MongoTemplate primaryMongoTemplate() {
		return new MongoTemplate(primaryMongoClient(), getPrimaryProperties().getDatabase());
	}
	
	@Primary
	@Bean(name = "primaryMongoClient")
	public MongoClient primaryMongoClient() {
		return MongoClients.create(getPrimaryProperties().getUri());
	}
}
