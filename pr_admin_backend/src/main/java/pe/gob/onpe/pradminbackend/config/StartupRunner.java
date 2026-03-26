package pe.gob.onpe.pradminbackend.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeImportar;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeModulo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

@Component
@Log4j2
public class StartupRunner implements ApplicationRunner {
	
    private final MongoTemplate mongoTemplate;
	private final Gson gson = new Gson();
	
	public StartupRunner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    	createCollectionAndInsertData(MaeImportar.class, "json/maeImportar.json");
        createCollectionAndInsertData(MaeModulo.class, "json/maeModulo.json");
    }

    private <T> void createCollectionAndInsertData(Class<T> entityClass, String jsonPath) {
        if (!mongoTemplate.collectionExists(entityClass)) {
            mongoTemplate.createCollection(entityClass);

            List<T> entities = loadJsonData(jsonPath, entityClass);
            if (entities != null && !entities.isEmpty()) {
                mongoTemplate.insert(entities, entityClass);
                log.info("Colección {} creada y datos insertados correctamente.", entityClass.getSimpleName());
            } else {
                log.warn("No se insertaron datos en {} porque la lista estaba vacía o nula.", entityClass.getSimpleName());
            }
        } else {
            log.info("La colección {} ya existe, no es necesario crearla nuevamente.", entityClass.getSimpleName());
        }
    }

    private <T> List<T> loadJsonData(String jsonPath, Class<T> entityClass) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonPath);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             JsonReader jsonReader = new JsonReader(inputStreamReader)) {

            Type listType = TypeToken.getParameterized(List.class, entityClass).getType();
            return gson.fromJson(jsonReader, listType);

        } catch (Exception e) {
            log.error("Error al cargar datos desde {}: ", jsonPath, e);
            return null;
        }
    }
}
