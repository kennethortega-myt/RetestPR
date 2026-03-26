package pe.gob.onpe.pradminbackend.model.bd.service.reportes.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.repository.*;
import pe.gob.onpe.pradminbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ResumenGeneralReporteService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ActaRespuestaReporteDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.FiltroActaEleccionReporteDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.FiltroParticipacionDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ParticipacionTotalesResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.resumengeneral.FiltroActaEleccionDto;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionMayusculaEnum;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumenGeneralReporteServiceImpl implements ResumenGeneralReporteService {

    public static final String DISTRITO_ELECTORAL = "distrito_electoral";
    public static final String ELECCION = "eleccion";
    public static final String TOTAL = "total";

    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final MaeFechaRepository maeFechaRepository;
    private final ParticipacionCiudadanaService participacionCiudadanaService;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

    @Override
    public Optional<ActaRespuestaReporteDto> obtenerTotalesPorEleccionParaReporte(FiltroActaEleccionReporteDto filtros) {
        if (filtros.getTipoFiltro().equals(DISTRITO_ELECTORAL) && filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) == 0) {
            filtros.setIdDistritoElectoral(15);
        }
        Optional<ActaRespuestaReporteDto> totales = this.obtenerVotosOrgPoliticaParaReporte(filtros);

        totales.ifPresent(total ->  {

            FiltroParticipacionDto filtroParticipacionCiudadanaDto = FiltroParticipacionDto.builder()
                    .tipoFiltro(filtros.getTipoFiltro().equals(ELECCION)? TOTAL :filtros.getTipoFiltro())
                    .idAmbitoGeografico(filtros.getIdAmbitoGeografico())
                    .ubigeoNivel01(filtros.getIdUbigeoDepartamento())
                    .ubigeoNivel02(filtros.getIdUbigeoProvincia())
                    .ubigeoNivel03(filtros.getIdUbigeoDistrito())
                    .idLocalVotacion(null)
                    .build();

            Optional<ParticipacionTotalesResponseDto> participacionTotalesResponseDto = participacionCiudadanaService.obtenerTotales(filtroParticipacionCiudadanaDto);
            participacionTotalesResponseDto.ifPresent(participacion ->  {
                total.setTotalElectoresHabiles(participacion.getTotalElectoresHabiles());
                total.setTotalAsistentes(participacion.getTotalAsistentes());
                total.setTotalAusentes(participacion.getTotalAusentes());
            });

            Optional<MaeFecha> fechaProceso = maeFechaRepository.findById(1);
            fechaProceso.ifPresent(fecha -> total.setFechaActualizacion(fecha.getFechaProceso()));

        });

        return totales;

    }

    private Optional<ActaRespuestaReporteDto> obtenerVotosOrgPoliticaParaReporte(FiltroActaEleccionReporteDto filtros){

        if (filtros.getTipoFiltro().equals(DISTRITO_ELECTORAL) && filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) == 0) {
            filtros.setIdDistritoElectoral(15);
        }
        Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
        Predicate<FiltroActaEleccionDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
        Predicate<FiltroActaEleccionDto> tieneDistritoElectoral = data -> data.getIdDistritoElectoral()!= null && data.getIdDistritoElectoral() != 0;

        FiltroActaEleccionDto filtrosDto = FiltroActaEleccionDto.builder()
                .tipoFiltro(filtros.getTipoFiltro())
                .idAmbitoGeografico(filtros.getIdAmbitoGeografico())
                .idUbigeoDepartamento(filtros.getIdUbigeoDepartamento())
                .idUbigeoProvincia(filtros.getIdUbigeoProvincia())
                .idUbigeoDistrito(filtros.getIdUbigeoDistrito())
                .idDistritoElectoral(filtros.getIdDistritoElectoral())
                .idEleccion(filtros.getIdEleccion())
                .build();

        List<VwPrPresidenciales> lstPresidencial;
        List<VwPrDiputados> lstDiputados;
        List<VwPrParlamentoAndino> lstParlamentoAndino;
        List<VwPrSenadoresDistritoElectoralMultiple> lstSenadores27;
        List<VwPrSenadoresDistritoNacionalUnico> lstSenadores33;
        List<VwPrRevocatoriaDistrital> lstRevocatoriaDistrital;

        TipoEleccionMayusculaEnum tipoDeEleccion = TipoEleccionMayusculaEnum.fromId(filtros.getIdEleccion().intValue()); // Devuelve SENADORES_DEU

        switch (tipoDeEleccion) {
            case PRESIDENCIAL:
                lstPresidencial = obtenerResumenPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtrosDto);
                if (lstPresidencial.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstPresidencial.get(0),filtros);
                }
            case DIPUTADOS:
                lstDiputados = obtenerResumenDiputados(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtrosDto);
                if (lstDiputados.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstDiputados.get(0),filtros);
                }
            case PARLAMENTO_ANDINO:
                lstParlamentoAndino = obtenerResumenParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtrosDto);
                if (lstParlamentoAndino.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstParlamentoAndino.get(0),filtros);
                }
            case SENADORES_27:
                lstSenadores27 = obtenerResumenSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtrosDto);
                if (lstSenadores27.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstSenadores27.get(0),filtros);
                }
            case SENADORES_33:
                lstSenadores33 = obtenerResumenSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtrosDto);
                if (lstSenadores33.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstSenadores33.get(0),filtros);
                }
            case REVOCATORIA_DISTRITAL:
                lstRevocatoriaDistrital = obtenerRevocatoriaDistrital(tieneEleccionAndFiltro, tieneAmbito, tieneUbigeo1, tieneUbigeo2, tieneUbigeo3, filtrosDto);
                if (lstRevocatoriaDistrital.isEmpty()){
                    return Optional.empty();
                } else {
                    return obtenerVotosOrgPoliticaGenerico(lstRevocatoriaDistrital.get(0),filtros);
                }
            default:
                return Optional.empty();
        }
    }

    private  Optional<ActaRespuestaReporteDto> obtenerVotosOrgPoliticaGenerico(VwPrEleccionBase registro, FiltroActaEleccionReporteDto filtros){
        int votosOrgPolitica = 0;
        if (registro.getDetalle() != null && !registro.getDetalle().isEmpty())  {
            votosOrgPolitica = registro.getDetalle().stream()
                    .filter(Objects::nonNull)
                    .filter(data -> Objects.nonNull(data.getEstado()))
                    .filter(data -> data.getEstado().compareTo(1) == 0)
                    .distinct()
                    .filter(data -> Objects.nonNull(data.getGrafico()))
                    .filter(data -> data.getGrafico() == 1)
                    .filter(data -> data.getCodigo().equals(filtros.getCodigoOp()))
                    .findFirst()
                    .map(VwPrEleccionBaseDetalle::getVotos)
                    .orElse(0);
        }

        return Optional.of(ActaRespuestaReporteDto.builder()
                .contabilizadas(registro.getActasContabilizadas())
                .enviadasJee(registro.getActasObservadasEnviadas())
                .pendientesJee(registro.getActasPendientes())
                .totalActas(registro.getTotalActas())
                .votosOrgPolitica(votosOrgPolitica)
                .build());
    }

    private List<VwPrPresidenciales> obtenerResumenPresidencial(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();

    }

    private List<VwPrDiputados> obtenerResumenDiputados(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneDistritoElectoral,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneDistritoElectoral.test(filtros) && filtros.getIdDistritoElectoral() == 30) {
            filtros.setIdDistritoElectoral(null);
            filtros.setTipoFiltro(ELECCION);
        }

        if(tieneEleccionAndFiltro.and(tieneDistritoElectoral.negate()).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), filtros.getTipoFiltro(), filtros.getIdDistritoElectoral());
        } else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), filtros.getTipoFiltro(), filtros.getIdDistritoElectoral());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();
    }

    private List<VwPrSenadoresDistritoElectoralMultiple> obtenerResumenSenadores27(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneDistritoElectoral,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneDistritoElectoral.test(filtros) && filtros.getIdDistritoElectoral() == 30) {
            filtros.setIdDistritoElectoral(null);
            filtros.setTipoFiltro(ELECCION);
        }
        if(tieneEleccionAndFiltro.and(tieneDistritoElectoral.negate()).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), filtros.getTipoFiltro(), filtros.getIdDistritoElectoral());
        } else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), filtros.getTipoFiltro(), filtros.getIdDistritoElectoral());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();
    }

    private List<VwPrParlamentoAndino> obtenerResumenParlamentoAndino(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();
    }

    private List<VwPrSenadoresDistritoNacionalUnico> obtenerResumenSenadores33(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();
    }

    private List<VwPrRevocatoriaDistrital> obtenerRevocatoriaDistrital(
            Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
            Predicate<FiltroActaEleccionDto> tieneAmbito,
            Predicate<FiltroActaEleccionDto> tieneUbigeo1,
            Predicate<FiltroActaEleccionDto> tieneUbigeo2,
            Predicate<FiltroActaEleccionDto> tieneUbigeo3,
            FiltroActaEleccionDto filtros) {

        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia());
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito());
        }
        return Collections.emptyList();
    }

}
