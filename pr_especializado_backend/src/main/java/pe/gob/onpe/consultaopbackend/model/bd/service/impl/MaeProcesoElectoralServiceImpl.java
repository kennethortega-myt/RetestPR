package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pe.gob.onpe.consultaopbackend.model.bd.documents.DetCatalogoEstructura;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.DetCatalogoEstructuraRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeProcesoElectoralRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeProcesoElectoralRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.consultaopbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.ProcesoElectoralActivoResponse;
import pe.gob.onpe.consultaopbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.consultaopbackend.utils.DateUtil;

import java.util.List;
import java.util.Optional;

@Service
public class MaeProcesoElectoralServiceImpl implements MaeProcesoElectoralService {

    @Autowired
    private MaeProcesoElectoralRepository maeProcesoElectoralRepository;

    @Autowired
    private MaeProcesoElectoralRepositoryCustom maeProcesoElectoralRepositoryCustom;

    @Autowired
    private DetCatalogoEstructuraRepositoryCustom detCatalogoEstructuraRepositoryCustom;

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
        MaeProcesoElectoral model = this.maeProcesoElectoralRepository.findBynActivo(1L);
        ProcesoElectoralActivoResponse response = null;
        if(model!=null) {
            response = new ProcesoElectoralActivoResponse();
            response.setId(model.getId());
            response.setNombre(model.getCNombre());
            response.setAcronimo(model.getCAcronimo());
            response.setFechaConvocatoria(DateUtil.sumarHoras(model.getDFechaConvocatoria(), 5));
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

        Long idAmbitoElectoral = proceso.getNTipoAmbitoElectoral();
        Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
        String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;

        DetCatalogoEstructura estructura = this.detCatalogoEstructuraRepositoryCustom.getDetCatalogoEstructura(idTablaCatalogo, columna, idAmbitoElectoral);

        if(proceso!=null) {
            procesoAmb.setIdProceso(proceso.getId());
            procesoAmb.setNombreProceso(proceso.getCNombre());
        }

        if(estructura!=null) {
            procesoAmb.setIdTipoAmbito(estructura.getNCodigo());
            procesoAmb.setNombreTipoAmbito(estructura.getCCodigo());
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
            Long idTipoAmbitoElectoral = proceso.getNTipoAmbitoElectoral();
            Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
            String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;

            DetCatalogoEstructura estructura = this.detCatalogoEstructuraRepositoryCustom.getDetCatalogoEstructura(idTablaCatalogo, columna, idTipoAmbitoElectoral);

            if(proceso!=null) {
                procesoAmb.setIdProceso(proceso.getId());
                procesoAmb.setNombreProceso(proceso.getCNombre());
            }

            if(estructura!=null) {
                procesoAmb.setIdTipoAmbito(estructura.getNCodigo());
                procesoAmb.setNombreTipoAmbito(estructura.getCCodigo());
            }
        }


        return procesoAmb;
    }

    @Override
    public Boolean validarProcesoActivo() {
        List<MaeProcesoElectoral> list = this.maeProcesoElectoralRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().allMatch(p -> p.getNActivo() != null && p.getNActivo() == 1);
    }

}
