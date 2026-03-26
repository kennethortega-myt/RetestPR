package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrMesa;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.FiltroMesaTotalesDto;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.MesaTotalesDto;

public interface VwPrMesaService extends CrudService<VwPrMesa> {

    Optional<MesaTotalesDto> obtenerMesasTotales(FiltroMesaTotalesDto filtroMesaTotalesDto);

}
