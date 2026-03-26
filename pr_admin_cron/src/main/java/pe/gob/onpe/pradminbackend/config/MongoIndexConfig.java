package pe.gob.onpe.pradminbackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    public MongoIndexConfig(@Qualifier("primaryMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void ensureIndexes() {

        String collectionPrActas = "vw_pr_acta";
        String collectionPrPresidenteVicepresidente = "vw_pr_presidente_y_vicepresidentes";
        String collectionPrSenadoresDistritoUnico = "vw_pr_senadores_distrito_unico";
        String collectionPrParlamentoAndino = "vw_pr_parlamento_andino";
        String collectionPrSenadoresDistritoMultiple = "vw_pr_senadores_distrito_multiple";
        String collecionPrDiputados = "vw_pr_diputados";

        String cTipoFiltro = "c_tipo_filtro";
        String nTipoEleccion = "n_tipo_eleccion";
        String nAmbitoGeografico = "n_ambito_geografico";
        String nUbigeoNivel01 = "n_ubigeo_nivel_01";
        String nUbigeoNivel02 = "n_ubigeo_nivel_02";
        String nUbigeoNivel03 = "n_ubigeo_nivel_03";

        createCollection(collectionPrActas);
        createCollection(collectionPrPresidenteVicepresidente);
        createCollection(collectionPrSenadoresDistritoUnico);
        createCollection(collectionPrParlamentoAndino);
        createCollection(collectionPrSenadoresDistritoMultiple);
        createCollection(collecionPrDiputados);

        mongoTemplate.indexOps(collectionPrActas)
                .ensureIndex(new Index()
                        .on("n_ubigeo", Sort.Direction.ASC)
                        .on(nAmbitoGeografico, Sort.Direction.ASC)
                        .named("inx_vw_pr_acta_01"));

        mongoTemplate.indexOps(collectionPrPresidenteVicepresidente)
                .ensureIndex(new Index()
                        .on(cTipoFiltro, Sort.Direction.ASC)
                        .on(nTipoEleccion, Sort.Direction.ASC)
                        .on(nAmbitoGeografico, Sort.Direction.ASC)
                        .on(nUbigeoNivel01, Sort.Direction.ASC)
                        .on(nUbigeoNivel02, Sort.Direction.ASC)
                        .on(nUbigeoNivel03, Sort.Direction.ASC)
                        .named("inx_vw_pr_presidente_y_vicepresidentes_01"));

        mongoTemplate.indexOps(collectionPrSenadoresDistritoUnico)
                .ensureIndex(new Index()
                        .on(cTipoFiltro, Sort.Direction.ASC)
                        .on(nTipoEleccion, Sort.Direction.ASC)
                        .on(nAmbitoGeografico, Sort.Direction.ASC)
                        .on(nUbigeoNivel01, Sort.Direction.ASC)
                        .on(nUbigeoNivel02, Sort.Direction.ASC)
                        .on(nUbigeoNivel03, Sort.Direction.ASC)
                        .named("inx_vw_pr_senadores_distrito_unico_01"));

        mongoTemplate.indexOps(collectionPrParlamentoAndino)
                .ensureIndex(new Index()
                        .on(cTipoFiltro, Sort.Direction.ASC)
                        .on(nTipoEleccion, Sort.Direction.ASC)
                        .on(nAmbitoGeografico, Sort.Direction.ASC)
                        .on(nUbigeoNivel01, Sort.Direction.ASC)
                        .on(nUbigeoNivel02, Sort.Direction.ASC)
                        .on(nUbigeoNivel03, Sort.Direction.ASC)
                        .named("inx_vw_pr_parlamento_andino_01"));

        mongoTemplate.indexOps(collectionPrSenadoresDistritoMultiple)
                .ensureIndex(new Index()
                        .on(cTipoFiltro, Sort.Direction.ASC)
                        .on(nTipoEleccion, Sort.Direction.ASC)
                        .named("inx_vw_pr_senadores_distrito_multiple_01"));

        mongoTemplate.indexOps(collecionPrDiputados)
                .ensureIndex(new Index()
                        .on(cTipoFiltro, Sort.Direction.ASC)
                        .on(nTipoEleccion, Sort.Direction.ASC)
                        .named("inx_vw_pr_diputados_01"));

    }

    @PostConstruct
    public void initIndexes() {
        ensureIndexes();
    }

    private void createCollection(String nameCollection){
        if (!mongoTemplate.collectionExists(nameCollection)) {
            mongoTemplate.createCollection(nameCollection);
        }
    }
}
