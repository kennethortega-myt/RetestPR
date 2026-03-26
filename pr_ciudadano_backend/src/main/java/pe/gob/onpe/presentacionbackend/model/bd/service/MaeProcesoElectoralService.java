package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.presentacionbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.ProcesoElectoralActivoResponse;

public interface MaeProcesoElectoralService extends CrudService<MaeProcesoElectoral> {

    List<MaeProcesoElectoral> findAll();

    ProcesoElectoralActivoResponse findByActivo();
    
    public ProcesoAmbitoDto getTipoAmbito(String acronimo);
    
    public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso);
}
