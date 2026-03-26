package pe.gob.onpe.pradminbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableMongoRepositories(
        basePackages = "pe.gob.onpe.pradminbackend.model.bd.repository",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = ".*\\.secondary\\..*"
        ),
        mongoTemplateRef = "primaryMongoTemplate"
)
public class MongoPrimaryConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    /**
     * Extrae el nombre de la base de datos desde la URI de MongoDB.
     * Ejemplo: mongodb://admin:password123@192.168.44.42:27017/pr_eg1310?authSource=admin
     * Retorna: pr_eg1310
     */
    private String extractDatabaseNameFromUri(String uri) {
        try {
            // Reemplazar "mongodb://" con "http://" para que URI pueda parsearlo
            String uriForParsing = uri.replace("mongodb://", "http://").replace("mongodb+srv://", "http://");
            URI parsedUri = new URI(uriForParsing);
            String path = parsedUri.getPath();

            if (path != null && !path.isEmpty() && !path.equals("/")) {
                // Remover la barra inicial y cualquier parámetro de query
                String dbName = path.substring(1); // Remover la barra inicial
                int questionMarkIndex = dbName.indexOf('?');
                if (questionMarkIndex > 0) {
                    dbName = dbName.substring(0, questionMarkIndex);
                }
                return dbName;
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Error al parsear la URI de MongoDB: " + uri, e);
        }
        throw new IllegalArgumentException("No se pudo extraer el nombre de la base de datos de la URI: " + uri);
    }

    @Primary
    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate primaryMongoTemplate() {
        String databaseName = extractDatabaseNameFromUri(mongoUri);
        return new MongoTemplate(primaryMongoClient(), databaseName);
    }

    @Primary
    @Bean(name = "primaryMongoClient")
    public MongoClient primaryMongoClient() {
        return MongoClients.create(mongoUri);
    }
}
