package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoEstructura;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.DetCatalogoEstructuraRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeEleccionRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeProcesoElectoralRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeProcesoElectoralRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.presentacionbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.ProcesoElectoralActivoResponse;
import pe.gob.onpe.presentacionbackend.utils.ConstantesCatalogo;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaeProcesoElectoralServiceImpl implements MaeProcesoElectoralService {


    private final MaeProcesoElectoralRepository maeProcesoElectoralRepository;
    private final MaeProcesoElectoralRepositoryCustom maeProcesoElectoralRepositoryCustom;
    private final DetCatalogoEstructuraRepositoryCustom detCatalogoEstructuraRepositoryCustom;
	private final MaeEleccionRepository maeEleccionRepository;

    @Override
    public void save(MaeProcesoElectoral k) {
        this.maeProcesoElectoralRepository.save(k);
    }

    @Override
    public void saveAll(List<MaeProcesoElectoral> k) {
        this.maeProcesoElectoralRepository.saveAll(k);
    }

    public List<MaeProcesoElectoral> findAll() {
        return this.maeProcesoElectoralRepository.findAll();
    }

    @Override
    public ProcesoElectoralActivoResponse findByActivo() {
		MaeProcesoElectoral model = this.maeProcesoElectoralRepository.findByActivo(1);
		ProcesoElectoralActivoResponse response = null;
		if(model!=null) {
			Optional<MaeEleccion> eleccionPrincipal = maeEleccionRepository.findByProcesoElectoralAndPrincipalAndActivo(model,1,1);

			response = new ProcesoElectoralActivoResponse();
			response.setId(model.getId());
			response.setNombre(model.getNombre());
			response.setAcronimo(model.getAcronimo());
			response.setFechaProceso(model.getFechaConvocatoria());
			response.setTipoProcesoElectoral(model.getTipoProcesoElectoral());

			if (eleccionPrincipal.isPresent()) {
				response.setIdEleccionPrincipal(Integer.parseInt(eleccionPrincipal.get().getCodigo()));
			}

		}
        return response;
    }

    @Override
    public void deleteAll() {
        this.maeProcesoElectoralRepository.deleteAll();
    }
    
    @Override
    public ProcesoAmbitoDto getTipoAmbito(String acronimo) {
    	ProcesoAmbitoDto procesoAmb = new ProcesoAmbitoDto();
    	MaeProcesoElectoral proceso = this.maeProcesoElectoralRepositoryCustom.getProcesoPorAcronimo(acronimo);

		Long idAmbitoElectoral = 0L;
		if(proceso != null) {
			procesoAmb.setIdProceso(proceso.getId());
			procesoAmb.setNombreProceso(proceso.getNombre());
			idAmbitoElectoral = proceso.getTipoAmbitoElectoral();
		}

    	Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
    	String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;
    	
    	DetCatalogoEstructura estructura = this.detCatalogoEstructuraRepositoryCustom.getDetCatalogoEstructura(idTablaCatalogo, columna, idAmbitoElectoral);
    	

    	
    	if(estructura!=null) {
    		procesoAmb.setIdTipoAmbito(estructura.getCodigo());
    		procesoAmb.setNombreTipoAmbito(estructura.getScodigo());
    	}
    	
    	return procesoAmb;
    }
    
    @Override
    public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso) {
    	
    	ProcesoAmbitoDto procesoAmb = null;
    	Optional<MaeProcesoElectoral> procesoOp = this.maeProcesoElectoralRepository.findById(idProceso);
    	
    	if(procesoOp.isPresent()) {
    		
    		procesoAmb = new ProcesoAmbitoDto();
    		MaeProcesoElectoral proceso = procesoOp.get();
    		Long idTipoAmbitoElectoral = proceso.getTipoAmbitoElectoral();
        	Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
        	String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;
        	
        	DetCatalogoEstructura estructura = this.detCatalogoEstructuraRepositoryCustom.getDetCatalogoEstructura(idTablaCatalogo, columna, idTipoAmbitoElectoral);
        	

			procesoAmb.setIdProceso(proceso.getId());
			procesoAmb.setNombreProceso(proceso.getNombre());

        	
        	if(estructura!=null) {
        		procesoAmb.setIdTipoAmbito(estructura.getCodigo());
        		procesoAmb.setNombreTipoAmbito(estructura.getScodigo());
        	}
    	}
    	
    	
    	return procesoAmb;
    }

}
