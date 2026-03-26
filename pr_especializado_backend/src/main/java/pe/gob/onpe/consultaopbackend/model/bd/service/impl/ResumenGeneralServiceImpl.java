package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.*;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.FiltroParticipacionDto;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.ParticipacionTotalesResponseDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.*;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumenGeneralServiceImpl implements ResumenGeneralService {

    public static final String DISTRITO_ELECTORAL = "distrito_electoral";
    public static final String SENADORES_33 = "Senadores Distrito Electoral Único";
    public static final String SENADORES_27 = "Senadores Distrito Electoral Múltiple";
    public static final String PRESIDENCIAL = "Presidencial";
    public static final String DIPUTADOS = "Diputados";
    public static final String PARLAMENTO_ANDINO = "Parlamento Andino";
    public static final String REVOCATORIA_DISTRITAL = "Revocatoria Distrital";
    public static final String ELECCION = "eleccion";
    public static final String TOTAL = "total";

    private final MaeResumenGeneralRepositoryCustom resumenGeneralRepositoryCustom;
    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final MaeFechaRepository maeFechaRepository;
    private final ParticipacionCiudadanaService participacionCiudadanaService;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

    @Override
    public Optional<ActaResDto> obtenerTotalesPorEleccion(FiltroActaEleccionDto filtros) {
        if (filtros.getTipoFiltro().equals(DISTRITO_ELECTORAL) && filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) == 0) {
            filtros.setIdDistritoElectoral(15);
        }
        Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
        Predicate<FiltroActaEleccionDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
        Predicate<FiltroActaEleccionDto> tieneDistritoElectoral = data -> data.getIdDistritoElectoral()!= null && data.getIdDistritoElectoral() != 0;

        Optional<ActaResDto> dto;
        List<VwPrPresidenciales> lstPresidencial;
        List<VwPrDiputados> lstDiputados;
        List<VwPrParlamentoAndino> lstParlamentoAndino;
        List<VwPrSenadoresDistritoElectoralMultiple> lstSenadores27;
        List<VwPrSenadoresDistritoNacionalUnico> lstSenadores33;
        List<VwPrRevocatoriaDistrital> lstRevocatoriaDistrital;
        switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
            case PRESIDENCIAL:
                lstPresidencial = obtenerResumenPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
                dto = optPresidencial(lstPresidencial);
                break;
            case DIPUTADOS:
                lstDiputados = obtenerResumenDiputados(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
                dto = optDiputados(lstDiputados);
                break;
            case PARLAMENTO_ANDINO:
                lstParlamentoAndino = obtenerResumenParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
                dto = optParlamentoAndino(lstParlamentoAndino);
                break;
            case SENADORES_27:
                lstSenadores27 = obtenerResumenSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
                dto = optSenadores27(lstSenadores27);
                break;
            case SENADORES_33:
                lstSenadores33 = obtenerResumenSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
                dto = optSenadores33(lstSenadores33);
                break;
            case REVOCATORIA_DISTRITAL:
                lstRevocatoriaDistrital = obtenerRevocatoriaDistrital(tieneEleccionAndFiltro, tieneAmbito, tieneUbigeo1, tieneUbigeo2, tieneUbigeo3, filtros);
                dto = optRevocatoriaDistrital(lstRevocatoriaDistrital);
                break;
            default:
                dto = Optional.empty();
        }

        FiltroParticipacionDto filtroParticipacionCiudadanaDto = FiltroParticipacionDto.builder()
                .tipoFiltro(filtros.getTipoFiltro().equals(ELECCION)? TOTAL :filtros.getTipoFiltro())
                .idAmbitoGeografico(filtros.getIdAmbitoGeografico())
                .ubigeoNivel01(filtros.getIdUbigeoDepartamento())
                .ubigeoNivel02(filtros.getIdUbigeoProvincia())
                .ubigeoNivel03(filtros.getIdUbigeoDistrito())
                .idLocalVotacion(null)
                .build();
        Optional<ParticipacionTotalesResponseDto> participacionTotalesResponseDto = participacionCiudadanaService.obtenerTotales(filtroParticipacionCiudadanaDto);
        if(participacionTotalesResponseDto.isPresent() && dto.isPresent()) {
            dto.get().setTotalElectoresHabiles(participacionTotalesResponseDto.get().getTotalElectoresHabiles());
            dto.get().setTotalAsistentes(participacionTotalesResponseDto.get().getTotalAsistentes());
            dto.get().setTotalAusentes(participacionTotalesResponseDto.get().getTotalAusentes());
        }

        asignarUbigeoPorDefectoPorEleccion(dto,filtros.getIdEleccion());
        asignarFechaProceso(dto);
        return dto;
    }

    private void asignarUbigeoPorDefectoPorEleccion(Optional<ActaResDto> dto,Integer idEleccion){
        if(dto.isPresent()) {
            if(idEleccion == 7 ) { // revocatoria distrital
                dto.get().setIdUbigeoDepartamento(Long.parseLong("250000"));
                dto.get().setIdUbigeoProvincia(Long.parseLong("250200"));
                dto.get().setIdUbigeoDistrito(Long.parseLong("250206"));
            } else { // eleccion general
                dto.get().setIdUbigeoDepartamento(Long.parseLong("140000"));
                dto.get().setIdUbigeoProvincia(Long.parseLong("140100"));
                dto.get().setIdUbigeoDistrito(Long.parseLong("140101"));
                dto.get().setIdUbigeoDistritoElectoral(15);
            }
        }
    }

    private void asignarFechaProceso(Optional<ActaResDto> actaEleccionDto){
        if(actaEleccionDto.isPresent()) {
            MaeFecha fechaProceso = maeFechaRepository.findById(1).orElse(MaeFecha.builder().fechaProceso(new Date()).build());
            actaEleccionDto.get().setFechaActualizacion(fechaProceso.getFechaProceso());
        }

    }

    private Optional<ActaResDto> optPresidencial(List<VwPrPresidenciales> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
    }

    private Optional<ActaResDto> optDiputados(List<VwPrDiputados> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
    }

    private Optional<ActaResDto> optSenadores27(List<VwPrSenadoresDistritoElectoralMultiple> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
    }

    private Optional<ActaResDto> optParlamentoAndino(List<VwPrParlamentoAndino> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
    }

    private Optional<ActaResDto> optSenadores33(List<VwPrSenadoresDistritoNacionalUnico> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
    }

    private Optional<ActaResDto> optRevocatoriaDistrital(List<VwPrRevocatoriaDistrital> lstaEleccion){
        return Optional.ofNullable(lstaEleccion)
                .filter(lista -> !lista.isEmpty())
                .map(registro -> ActaResDto.builder()
                        .contabilizadasPorcentaje(registro.get(0).getPorcentajeActasContabilizadas())
                        .contabilizadas(registro.get(0).getActasContabilizadas())
                        .totalActas(registro.get(0).getTotalActas())
                        .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
                        .enviadasJeePorcentaje(registro.get(0).getPorcentajeActasObservadasEnviadas())
                        .enviadasJee(registro.get(0).getActasObservadasEnviadas())
                        .pendientesJeePorcentaje(registro.get(0).getPorcentajeActasPendientes())
                        .pendientesJee(registro.get(0).getActasPendientes())
                        .fechaActualizacion(registro.get(0).getAudFechaModificacion())
                        .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
                        .totalElectoresHabiles(registro.get(0).getTotalElectoresHabiles())
                        .build()
                );
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
        switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
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

    @Override
    public List<ActaMapaCalorResponseDto> listarMapaCalor(ActaMapaCalorRequestDto filtros, String actaCodigoEstado) {
        Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro = data ->
                data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
                        && data.getIdEleccion() != null && data.getIdEleccion() != 0;
        Predicate<ActaMapaCalorRequestDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
        Predicate<ActaMapaCalorRequestDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
        Predicate<ActaMapaCalorRequestDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
        Predicate<ActaMapaCalorRequestDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

        return switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
            case PRESIDENCIAL -> obtenerDataPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
            case DIPUTADOS -> obtenerDataDiputados(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
            case PARLAMENTO_ANDINO ->obtenerDataParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
            case SENADORES_27 -> obtenerDataSenadores27(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
            case SENADORES_33 ->obtenerDataSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
            case REVOCATORIA_DISTRITAL -> obtenerDataRevocatoriaDistrital(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);

            default -> Collections.emptyList();
        };

    }

    private List<ActaMapaCalorResponseDto> obtenerDataPresidencial(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                   Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                   Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                   Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                   Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                   ActaMapaCalorRequestDto filtros,
                                                                   String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrPresidenciales> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return  construirRespuestaPresidencial(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return  construirRespuestaPresidencial(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return  construirRespuestaPresidencial(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return  construirRespuestaPresidencial(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaPresidencial(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> obtenerDataDiputados(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                ActaMapaCalorRequestDto filtros,
                                                                String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrDiputados> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> obtenerDataSenadores27(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                  ActaMapaCalorRequestDto filtros,
                                                                  String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrSenadoresDistritoElectoralMultiple> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> obtenerDataParlamentoAndino(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                       Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                       Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                       Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                       Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                       ActaMapaCalorRequestDto filtros,
                                                                       String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrParlamentoAndino> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return  construirRespuestaParlamentoAndino(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return  construirRespuestaParlamentoAndino(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return  construirRespuestaParlamentoAndino(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return  construirRespuestaParlamentoAndino(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaParlamentoAndino(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> obtenerDataSenadores33(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                  Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                  ActaMapaCalorRequestDto filtros,
                                                                  String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrSenadoresDistritoNacionalUnico> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return  construirRespuestaSenadores33(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return  construirRespuestaSenadores33(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return  construirRespuestaSenadores33(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return  construirRespuestaSenadores33(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaSenadores33(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> obtenerDataRevocatoriaDistrital(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
                                                                           Predicate<ActaMapaCalorRequestDto> tieneAmbito,
                                                                           Predicate<ActaMapaCalorRequestDto> tieneUbigeo1,
                                                                           Predicate<ActaMapaCalorRequestDto> tieneUbigeo2,
                                                                           Predicate<ActaMapaCalorRequestDto> tieneUbigeo3,
                                                                           ActaMapaCalorRequestDto filtros,
                                                                           String actaCodigoEstado) {

        String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

        List<VwPrRevocatoriaDistrital> registros = null;
        if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
            return  construirRespuestaRevocatoriaDistrital(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
            return  construirRespuestaRevocatoriaDistrital(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
            return  construirRespuestaRevocatoriaDistrital(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
            registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
            return  construirRespuestaRevocatoriaDistrital(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
            registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
            return construirRespuestaRevocatoriaDistrital(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ActaMapaCalorResponseDto> construirRespuestaPresidencial(List<VwPrPresidenciales> registros,Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }
    private List<ActaMapaCalorResponseDto> construirRespuestaDiputados(List<VwPrDiputados> registros, Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }
    private List<ActaMapaCalorResponseDto> construirRespuestaParlamentoAndino(List<VwPrParlamentoAndino> registros,Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }
    private List<ActaMapaCalorResponseDto> construirRespuestaSenadores27(List<VwPrSenadoresDistritoElectoralMultiple> registros, Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }
    private List<ActaMapaCalorResponseDto> construirRespuestaSenadores33(List<VwPrSenadoresDistritoNacionalUnico> registros,Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }

    private List<ActaMapaCalorResponseDto> construirRespuestaRevocatoriaDistrital(List<VwPrRevocatoriaDistrital> registros,Integer codigoAgrupacionPolitica, String actaCodigoEstado){

        return  registros.stream()
                .map(data -> ResumenGeneralServiceImpl.mapperMapaCalorGenerico(data,codigoAgrupacionPolitica, actaCodigoEstado))
                .toList();
    }

    private static ActaMapaCalorResponseDto mapperMapaCalorGenerico(VwPrEleccionBase registroResumen, Integer codigoAgrupacionPolitica, String actaCodigoEstado){
        ParticipanteDto participante = null;
        if(codigoAgrupacionPolitica != null && codigoAgrupacionPolitica.compareTo(0) != 0) {
            Optional<VwPrEleccionBaseDetalle> deltalleDiputado = registroResumen.getDetalle().stream()
                    .filter(detalle -> detalle.getAgrupacionPolitica().compareTo(codigoAgrupacionPolitica) == 0)
                    .findAny();

            if(deltalleDiputado.isPresent()){
                participante = ParticipanteDto.builder().build();
                VwPrEleccionBaseDetalle registroDetalle = deltalleDiputado.get();
                if(!registroDetalle.getCandidato().isEmpty()){
                    String nombre = registroDetalle.getCandidato().get(0).getNombres();
                    String apellido = registroDetalle.getCandidato().get(0).getApellidoPaterno();
                    participante.setNombreCandidato(nombre + " " + apellido);
                }
                participante.setPorcentajeVotosValidos(registroDetalle.getPorcentajeVotosValidos());
                participante.setTotalVotosValidos(registroDetalle.getVotos());
            }
        }
        if(actaCodigoEstado.equals("C") || actaCodigoEstado.equals("H")) {
            return ActaMapaCalorResponseDto.builder()
                    .porcentajeActasContabilizadas(registroResumen.getPorcentajeActasContabilizadas())
                    .actasContabilizadas(registroResumen.getActasContabilizadas())
                    .ambitoGeografico(registroResumen.getAmbitoGeografico())
                    .ubigeoNivel01(registroResumen.getUbigeoNivel01())
                    .ubigeoNivel02(registroResumen.getUbigeoNivel02())
                    .ubigeoNivel03(registroResumen.getUbigeoNivel03())
                    .distritoElectoral(registroResumen.getDistritoElectoral())
                    .participante(participante)
                    .build();
        }
        return null;
    }

    private String obtenerTipoFiltro(String tipoFiltro)  {

        return switch (tipoFiltro) {
            case TOTAL -> ELECCION;
            case ELECCION -> "ambito_geografico";
            case "ambito_geografico" -> "ubigeo_nivel_01";
            case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
            case "ubigeo_nivel_02" -> "ubigeo_nivel_03";
            case "ubigeo_nivel_03" -> "local_votacion";
            case DISTRITO_ELECTORAL -> DISTRITO_ELECTORAL;
            default -> "";
        };
    }

    @Override
    public List<VistaResumenGeneralDto> obtenerElecciones(FiltroEleccionesDto filtro) {

        List<MaeEleccion> lstEleccion = resumenGeneralRepositoryCustom.findEleccionesByProceso(filtro.getIdProceso(), filtro.getActivo());

        if (lstEleccion.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> lstIdEleccion = lstEleccion.stream().map(maeEleccion -> maeEleccion.getId().intValue()).toList();
        List<VistaResumenGeneralDto> resultados = new ArrayList<>();

        FiltroActaEleccionDto filtros = FiltroActaEleccionDto.builder()
                .tipoFiltro(filtro.getTipoFiltro())
                .idAmbitoGeografico(filtro.getIdAmbitoGeografico())
                .idUbigeoDepartamento(filtro.getUbigeoNivel01())
                .idUbigeoProvincia(filtro.getUbigeoNivel02())
                .idUbigeoDistrito(filtro.getUbigeoNivel03())
                .build();
        Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
        Predicate<FiltroActaEleccionDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
        Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
        Predicate<FiltroActaEleccionDto> tieneDistritoElectoral = data -> data.getIdDistritoElectoral()!= null && data.getIdDistritoElectoral() != 0;

        if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.PRESIDENCIAL.getCodigo().toString()))) {
            filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.PRESIDENCIAL.getCodigo().toString()));
            resultados.addAll(procesarResultados(
                    lstEleccion,
                    this.obtenerResumenPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
            ));
        }

        if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.DIPUTADOS.getCodigo().toString()))) {
            filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.DIPUTADOS.getCodigo().toString()));
            resultados.addAll(procesarResultados(
                    lstEleccion,
                    this.obtenerResumenDiputados(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
            ));
        }

        if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo().toString()))) {
            filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo().toString()));
            resultados.addAll(procesarResultados(
                    lstEleccion,
                    this.obtenerResumenParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
            ));
        }

        if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.SENADORES_27.getCodigo().toString()))) {
            filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.SENADORES_27.getCodigo().toString()));
            resultados.addAll(procesarResultados(
                    lstEleccion,
                    this.obtenerResumenSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
            ));
        }

        if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.SENADORES_33.getCodigo().toString()))) {
            filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.SENADORES_33.getCodigo().toString()));
            resultados.addAll(procesarResultados(
                    lstEleccion,
                    this.obtenerResumenSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
            ));
        }

        return resultados;
    }

    private <T extends VwPrEleccionBase> List<VistaResumenGeneralDto> procesarResultados(
            List<MaeEleccion> lstEleccion,
            List<T> elecciones
    ) {
        if (elecciones.isEmpty()) {
            return Collections.emptyList();
        }

        return elecciones.stream()
                .map(vwPrEleccion -> {
                    Optional<MaeEleccion> eleccion = lstEleccion.stream()
                            .filter(e -> e.getId().equals(vwPrEleccion.getTipoEleccion().longValue()))
                            .findFirst();

                    if (eleccion.isEmpty()) {
                        return null; // Manejo de posibles casos nulos
                    }

                    VistaResumenGeneralDto dto = new VistaResumenGeneralDto();
                    dto.setId(Long.parseLong(eleccion.get().getCodigo()));
                    dto.setNombre(eleccion.get().getNombre());
                    dto.setActasContabilizadas(vwPrEleccion.getActasContabilizadas());
                    dto.setPorcentajeActasContabilizadas(vwPrEleccion.getPorcentajeActasContabilizadas());
                    dto.setActasObservadasEnviadas(vwPrEleccion.getActasObservadasEnviadas());
                    dto.setPorcentajeActasObservadasEnviadas(vwPrEleccion.getPorcentajeActasObservadasEnviadas());
                    dto.setActasPendientes(vwPrEleccion.getActasPendientes());
                    dto.setPorcentajeActasPendientes(vwPrEleccion.getPorcentajeActasPendientes());
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
