package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.DetUbigeoEleccion;
import pe.gob.onpe.consultaopbackend.model.dto.UbigeoDto;
import pe.gob.onpe.consultaopbackend.utils.DigitosUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class DetUbigeoEleccionRepositoryCustom {

    private final MongoOperations mongoOperations;

    public DetUbigeoEleccionRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void deleteByIdCentroComputoAndProceso(Long idCentroComputo, String proceso) {
        Criteria criteria = Criteria.where("id.nCentroComputo").is(idCentroComputo)
                .and("id.cAcronimoProceso").is(proceso);
        Query query = Query.query(criteria);
        mongoOperations.remove(query, DetUbigeoEleccion.class);
    }

    public UbigeoDto obtenerUbigeoAleatorioPorEleccion(Long idEleccion) {

        Query query = new Query(
                Criteria.where("eleccion.id").is(idEleccion)
        );

        List<DetUbigeoEleccion> detUbigeosEleccion =
                mongoOperations.find(query, DetUbigeoEleccion.class);

        if (detUbigeosEleccion.isEmpty()) {
            return UbigeoDto.builder().build();
        }

        int index = ThreadLocalRandom.current()
                .nextInt(detUbigeosEleccion.size());

        DetUbigeoEleccion ubigeoUnico = detUbigeosEleccion.get(index);

        Long idUbigeo = ubigeoUnico.getUbigeo().getId();
        Integer idDistritoElectoral =
                ubigeoUnico.getUbigeo().getNDistritoElectoral();

        Long idUbigeoDepartamento =
                DigitosUtils.obtenerUbigeos(List.of(idUbigeo), "DEPARTAMENTOS").get(0);

        Long idUbigeoProvincia =
                DigitosUtils.obtenerUbigeos(List.of(idUbigeo), "PROVINCIAS").get(0);

        return UbigeoDto.builder()
                .idUbigeoDepartamento(idUbigeoDepartamento)
                .idUbigeoProvincia(idUbigeoProvincia)
                .idUbigeoDistrito(idUbigeo)
                .idUbigeoDistritoElectoral(idDistritoElectoral)
                .build();
    }
}
