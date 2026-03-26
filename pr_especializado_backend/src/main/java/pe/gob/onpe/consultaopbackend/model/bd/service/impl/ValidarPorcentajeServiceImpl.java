package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.*;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ValidarPorcentajeService;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionMayusculaEnum;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidarPorcentajeServiceImpl implements ValidarPorcentajeService {

    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

    private static final String ELECCION = "eleccion";

    @Override
    public double obtenerPorcentageContabilizado(Integer idEleccion) {

        TipoEleccionMayusculaEnum tipoDeEleccion = TipoEleccionMayusculaEnum.fromId(idEleccion.intValue()); // Devuelve SENADORES_DEU

        switch (tipoDeEleccion) {
            case PRESIDENCIAL:
                List<VwPrPresidenciales> lista = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(lista.isEmpty()){
                    return 0.0;
                } else {
                    VwPrPresidenciales registro = lista.getFirst();
                    return registro.getPorcentajeActasContabilizadas();
                }
            case DIPUTADOS:
                List<VwPrDiputados> listaDiputados = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(listaDiputados.isEmpty()){
                    return 0.0;
                } else {
                    VwPrDiputados registro = listaDiputados.getFirst();
                    return registro.getPorcentajeActasContabilizadas();
                }
            case PARLAMENTO_ANDINO:
                List<VwPrParlamentoAndino> listaParlamento = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(listaParlamento.isEmpty()){
                    return 0.0;
                } else {
                    VwPrParlamentoAndino registro = listaParlamento.getFirst();
                    return registro.getPorcentajeActasContabilizadas();
                }
            case SENADORES_27:
                List<VwPrSenadoresDistritoElectoralMultiple> listaSenadoresMultiple = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(listaSenadoresMultiple.isEmpty()){
                    return 0.0;
                } else {
                    VwPrSenadoresDistritoElectoralMultiple registro = listaSenadoresMultiple.getFirst();
                    return registro.getPorcentajeActasContabilizadas();
                }
            case SENADORES_33:
                List<VwPrSenadoresDistritoNacionalUnico> listaDistritoUnico = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(listaDistritoUnico.isEmpty()){
                    return 0.0;
                } else {
                    VwPrSenadoresDistritoNacionalUnico registro = listaDistritoUnico.get(0);
                    return registro.getPorcentajeActasContabilizadas();
                }
            case REVOCATORIA_DISTRITAL:
                List<VwPrRevocatoriaDistrital> listaRevocatoria = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(idEleccion,ELECCION);
                if(listaRevocatoria.isEmpty()){
                    return 0.0;
                } else {
                    VwPrRevocatoriaDistrital registro = listaRevocatoria.get(0);
                    return registro.getPorcentajeActasContabilizadas();
                }
            default:
                return 0.0;
        }
    }
}
