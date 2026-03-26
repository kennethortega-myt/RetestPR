package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.consultaopbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.ProcesoElectoralActivoResponse;

import java.util.List;

public interface MaeProcesoElectoralService extends CrudService<MaeProcesoElectoral> {

    List<MaeProcesoElectoral> findAll();

    ProcesoElectoralActivoResponse findByActivo();

    public ProcesoAmbitoDto getTipoAmbito(String acronimo);

    public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso);

    Boolean validarProcesoActivo();
}
