package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeLocalVotacion;
import pe.gob.onpe.presentacionbackend.model.dto.FiltroUbigeoLocalVotacionDto;
import pe.gob.onpe.presentacionbackend.model.dto.UbigeoLocalVotacionDto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class MaeLocalVotacionRepositoryCustom {

    private final MongoOperations mongoOperations;

    public MaeLocalVotacionRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public List<UbigeoLocalVotacionDto> listarLocalVotacionPorIdEleccion(FiltroUbigeoLocalVotacionDto filtro) {
    	Criteria criteria = Criteria.where("ubigeo.id").is(filtro.getIdUbigeo());
    	Query query = Query.query(criteria);
    	List<MaeLocalVotacion> lstMaeLocalVotacion = mongoOperations.find(query, MaeLocalVotacion.class);
    	return !lstMaeLocalVotacion.isEmpty() ? lstMaeLocalVotacion.stream()
    			.map(maeLocalVotacion -> UbigeoLocalVotacionDto.builder()
    					.codigoLocalVotacion(maeLocalVotacion.getId())
    					.nombreLocalVotacion(maeLocalVotacion.getCNombre())
    					.build())
    			.sorted(Comparator.comparing(UbigeoLocalVotacionDto::getNombreLocalVotacion))
    			.toList():Collections.emptyList();
    }

}
