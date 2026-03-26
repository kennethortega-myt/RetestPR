package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrPresidenciales;
import pe.gob.onpe.presentacionbackend.model.dto.eleccionpresidencial.*;

public interface VwPrPresidencialesService extends CrudService<VwPrPresidenciales> {

    List<ParticipantePresidencialDto> listarParticipantesUbicacionGeografica(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);
    List<ParticipantePresidencialDto> listarParticipantesUbicacionGeograficaNombre(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);
    List<ParticipantePresidencialDto> listarParticipantesOrganizacionPolitica(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);

}

