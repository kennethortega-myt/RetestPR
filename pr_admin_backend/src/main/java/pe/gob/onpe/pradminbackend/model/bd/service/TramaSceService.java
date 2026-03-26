package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaSceDto;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaScePuestaCeroDto;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaResponse;

public interface TramaSceService {

    Optional<TramaVistaResponse> recibirTrama(List<TramaSceDto> listTramaSce);
    TramaScePuestaCeroDto puestaCero(String usuarioSce);
}
