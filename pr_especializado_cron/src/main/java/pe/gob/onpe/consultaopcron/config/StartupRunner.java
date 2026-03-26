package pe.gob.onpe.consultaopcron.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.ObjetoReporte10porciento;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.ObjetoReporte20porciento;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.ObjetoReporte5porciento;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteAutomatico;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Log4j2

public class StartupRunner implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    public StartupRunner(@Qualifier("secondaryMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createCollectionAndInsertData();
    }

    private void createCollectionAndInsertData() {
        if (!mongoTemplate.collectionExists(TabReporteAutomatico.class)) {
            mongoTemplate.createCollection(TabReporteAutomatico.class);
            mongoTemplate.insertAll(obtenerEleccionesIniciales());
        } else {
            log.info("La colección TabReporteAutomatico ya existe, no es necesario crearla nuevamente.");
        }
    }

    private static final String ELECCION_PRESIDENCIAL = "Presidencial";
    private static final String ELECCION_DIPUTADOS = "Diputados";
    private static final String ELECCION_PARLAMENTO = "Parlamento Andino";
    private static final String CRON_EXPRESSION = "0 0 */2 * * *";

    private List<TabReporteAutomatico> obtenerEleccionesIniciales() {

        TabReporteAutomatico presidencial = TabReporteAutomatico.builder()
                .eleccionId(10)
                .eleccion(ELECCION_PRESIDENCIAL)
                .fechaInicio(LocalDate.now())
                .horaInicio(LocalTime.now())
                .tipoReporte(1)
                .tipoGeneracionReporte(1)
                .expresionCron(CRON_EXPRESSION)
                .estado(0)
                .build();

        TabReporteAutomatico diputados = TabReporteAutomatico.builder()
                .eleccionId(13)
                .eleccion(ELECCION_DIPUTADOS)
                .fechaInicio(LocalDate.now())
                .horaInicio(LocalTime.now())
                .tipoReporte(1)
                .tipoGeneracionReporte(1)
                .expresionCron(CRON_EXPRESSION)
                .estado(0)
                .build();

        TabReporteAutomatico parlamento = TabReporteAutomatico.builder()
                .eleccionId(12)
                .eleccion(ELECCION_PARLAMENTO)
                .fechaInicio(LocalDate.now())
                .horaInicio(LocalTime.now())
                .tipoReporte(1)
                .tipoGeneracionReporte(1)
                .expresionCron(CRON_EXPRESSION)
                .estado(0)
                .build();

        return List.of(presidencial, diputados, parlamento);
    }

}
