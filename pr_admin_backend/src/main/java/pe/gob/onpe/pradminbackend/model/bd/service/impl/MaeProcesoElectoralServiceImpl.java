package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetCatalogoEstructura;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.pradminbackend.model.bd.repository.DetCatalogoEstructuraRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeProcesoElectoralRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeProcesoElectoralRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeProcesoElectoralService;

import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.pradminbackend.model.dto.response.ProcesoElectoralActivoResponse;
import pe.gob.onpe.pradminbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.pradminbackend.utils.DateUtil;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaeProcesoElectoralServiceImpl implements MaeProcesoElectoralService {


    private final MaeProcesoElectoralRepository maeProcesoElectoralRepository;

    private final MaeProcesoElectoralRepositoryCustom maeProcesoElectoralRepositoryCustom;

    private final DetCatalogoEstructuraRepositoryCustom detCatalogoEstructuraRepositoryCustom;

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
            response = new ProcesoElectoralActivoResponse();
            response.setId(model.getId());
            response.setNombre(model.getNombre());
            response.setAcronimo(model.getAcronimo());
            response.setFechaConvocatoria(DateUtil.sumarHoras(model.getFechaConvocatoria(), 5));
        }
        return response;
    }

    @Override
    public ProcesoAmbitoDto getTipoAmbito(String acronimo) {
        ProcesoAmbitoDto procesoAmb = new ProcesoAmbitoDto();
        MaeProcesoElectoral proceso = this.maeProcesoElectoralRepositoryCustom.getProcesoPorAcronimo(acronimo);

        Long idAmbitoElectoral = proceso.getTipoAmbitoElectoral();
        Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
        String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;

        DetCatalogoEstructura estructura = this.detCatalogoEstructuraRepositoryCustom.getDetCatalogoEstructura(idTablaCatalogo, columna, idAmbitoElectoral);

        if(proceso!=null) {
            procesoAmb.setIdProceso(proceso.getId());
            procesoAmb.setNombreProceso(proceso.getNombre());
        }

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

            if(proceso!=null) {
                procesoAmb.setIdProceso(proceso.getId());
                procesoAmb.setNombreProceso(proceso.getNombre());
            }

            if(estructura!=null) {
                procesoAmb.setIdTipoAmbito(estructura.getCodigo());
                procesoAmb.setNombreTipoAmbito(estructura.getScodigo());
            }
        }


        return procesoAmb;
    }

    @Override
    public void deleteAll() {
        this.maeProcesoElectoralRepository.deleteAll();
    }

}
