package pe.gob.onpe.presentacionbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;

@Component
@Slf4j
public class MongoConnectionTest implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoConnectionTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            long count = mongoTemplate.getCollection("ONPE-LOCAL").countDocuments();
            log.info("✅ Conexión exitosa a MongoDB. Documentos en mi_coleccion: " + count);
        } catch (Exception e) {
            log.error("❌ Error conectando a MongoDB: " + e.getMessage());
        }
    }
}
