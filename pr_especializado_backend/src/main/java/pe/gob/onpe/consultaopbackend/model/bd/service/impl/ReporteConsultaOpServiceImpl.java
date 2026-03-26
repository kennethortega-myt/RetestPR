package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteArchivoService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteConsultaOPService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ValidarPorcentajeService;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.*;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.utils.enums.EstadosReporteEnum;
import pe.gob.onpe.consultaopbackend.utils.ReporteUtils;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;

import java.util.*;
import java.util.function.Predicate;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteConsultaOpServiceImpl implements ReporteConsultaOPService {


    private final TabReporteRepository tabReporteRepository;
    private final ReporteArchivoService generarReporteAsincrono;
    private final ValidarPorcentajeService validarPorcentajeService;

    @Override
    public ResponseEntity<GenericResponse<String> > registrarReporteBackground(ReporteRequest requestReporte) {
        GenericResponse<String>  response = new GenericResponse<> ();
        response.setSuccess(false);
        String filtrosIdsJson = ReporteUtils.getJsonFromRequestIds(requestReporte);
        List<Integer> listaEstados = Arrays.asList(0,1);//estados (0: registrado, 1: en proceso)
        List<TabReporte> reportesPendientes = tabReporteRepository.findByEstadoInAndFiltro(listaEstados,filtrosIdsJson);

        if(!reportesPendientes.isEmpty()) {
            response.setMessage("Existe un reporte en proceso para los filtros ingresados.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        //json ids y valores
        String filtrosJson = ReporteUtils.getJsonFromRequest(requestReporte);
        //generar id reporte
        String idReporte = UUID.randomUUID().toString();
        TabReporte registroReporteInicial = TabReporte.builder()
                .id(idReporte)
                .filtro(filtrosIdsJson)
                .filtroValores(filtrosJson)
                .porcentaje(validarPorcentajeService.obtenerPorcentageContabilizado(
                        requestReporte.getIdEleccion()))
                .codigoUsuario(null != requestReporte.getCodigoOp() ? requestReporte.getCodigoOp() : requestReporte.getCodigoUsuario())
                .cAudUsuarioCreacion(null != requestReporte.getCodigoOp() ? requestReporte.getCodigoOp() : requestReporte.getCodigoUsuario())
                .fechaCreacion(new Date())
                .tipoReporte(requestReporte.getTipoReporte())
                .tipoEleccion(requestReporte.getIdEleccion())
                .nActivo(1)
                .estado(0)//registrado
                .build();
        tabReporteRepository.save(registroReporteInicial);
        log.info("Primer registro del reporte:::Registrado");
        if (requestReporte.getTipoReporte() == 1) {
            generarReporteAsincrono.generarReporteCsv(requestReporte,registroReporteInicial);
        } else if (requestReporte.getTipoReporte() == 2) {
            generarReporteAsincrono.generarReporteObservadosCsv(requestReporte,registroReporteInicial);
        } else {
            log.info("TIPO DE REPORTE NO MAPEADO EN SISCOP");
        }

        response.setMessage("Se esta generando su reporte.");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Override
    public ReporteHistorialPaginado listarReportesPorUsuario(ReporteHistorialRequestDto request, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio);
        Predicate<ReporteHistorialRequestDto> tieneUsuario = data -> data.getUsuarioConsulta() != null && !data.getUsuarioConsulta().isEmpty();
        Predicate<ReporteHistorialRequestDto> tieneTipoEleccion = data -> data.getTipoEleccion() != null && data.getTipoEleccion() != 0;
        Predicate<ReporteHistorialRequestDto> tieneTipoReporte = data -> data.getTipoReporte() != null && data.getTipoReporte() != 0;
        List<Integer> estadosExcluidos = Arrays.asList(
                EstadosReporteEnum.ESTADO_SIN_REGISTROS_BD.getCodigo(),
                EstadosReporteEnum.ESTADO_NO_SUBIDO_SFTP.getCodigo());
        Page<TabReporte> objetoTabReporte = null;
        if (tieneUsuario.and(tieneTipoEleccion.negate()).and(tieneTipoReporte.negate()).test(request)){
            objetoTabReporte = tabReporteRepository.findAllByCodigoUsuarioAndEstadoNotInOrderByFechaCreacionDesc(request.getUsuarioConsulta(), estadosExcluidos, pageable);
        } else if (tieneUsuario.and(tieneTipoEleccion).and(tieneTipoReporte.negate()).test(request)){
            objetoTabReporte = tabReporteRepository.findAllByCodigoUsuarioAndTipoEleccionAndEstadoNotInOrderByFechaCreacionDesc(request.getUsuarioConsulta(),request.getTipoEleccion(), estadosExcluidos, pageable);
        } else if (tieneUsuario.and(tieneTipoEleccion.negate()).and(tieneTipoReporte).test(request)){
            objetoTabReporte = tabReporteRepository.findAllByCodigoUsuarioAndTipoReporteAndEstadoNotInOrderByFechaCreacionDesc(request.getUsuarioConsulta(),request.getTipoReporte(), estadosExcluidos, pageable);
        } else if (tieneUsuario.and(tieneTipoEleccion).and(tieneTipoReporte).test(request)){
            objetoTabReporte = tabReporteRepository.findAllByCodigoUsuarioAndTipoEleccionAndTipoReporteAndEstadoNotInOrderByFechaCreacionDesc(request.getUsuarioConsulta(),request.getTipoEleccion(),request.getTipoReporte(), estadosExcluidos, pageable);
        } else {
            objetoTabReporte = Page.empty();
        }

        if (objetoTabReporte.isEmpty()) {
            log.info("No existen registros en bd para los filtros ingresados");
            return null;
        }

        return contruirRespuesta(objetoTabReporte);
    }

    @Override
    public ReporteHistorialPaginado listarReportesAutomaticos(ReporteAutomaticoRequestDto request, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        List<Integer> estadosExcluidos = Arrays.asList(
                EstadosReporteEnum.ESTADO_SIN_REGISTROS_BD.getCodigo(),
                EstadosReporteEnum.ESTADO_NO_SUBIDO_SFTP.getCodigo());
        Page<TabReporte> objetoTabReporte = null;
        if (Objects.isNull(request.getTipoEleccion())) {
            objetoTabReporte = tabReporteRepository
                    .findAllByCodigoUsuarioAndEstadoNotInOrderByFechaCreacionDesc(
                            request.getUsuarioConsulta(),
                            estadosExcluidos,
                            pageable);

        } else {
            objetoTabReporte = tabReporteRepository
                    .findByCodigoUsuarioAndTipoEleccionInFiltroValoresOrderByFechaCreacionDesc(
                            request.getUsuarioConsulta(),
                            TipoEleccionEnum.obtenerDescripcion(request.getTipoEleccion()),
                            pageable);

        }

        if (objetoTabReporte.isEmpty()) {
            log.info("No existen registros de reportes automaticos");
            return null;
        }

        return contruirRespuesta(objetoTabReporte);

    }

    private ReporteHistorialPaginado contruirRespuesta(Page<TabReporte> objetoTabReporte) {


        List<ReporteHistorialDto> registros = objetoTabReporte.getContent().stream()
                .map(ReporteConsultaOpServiceImpl::mapperReporte)
                .sorted(Comparator.comparing(ReporteHistorialDto::getFechaConsulta, Comparator.nullsLast(Date::compareTo)).reversed())
                .toList();

        return ReporteHistorialPaginado.builder()
                .paginaActual(objetoTabReporte.getNumber())
                .totalPaginas(objetoTabReporte.getTotalPages())
                .totalRegistros(objetoTabReporte.getTotalElements())
                .content(registros)
                .build();
    }

    private static ReporteHistorialDto mapperReporte(TabReporte reporte) {

        ReporteFiltrosValoresDto valores = ReporteUtils.getObjectFromJson(reporte.getFiltroValores());

        return ReporteHistorialDto.builder()
                .fechaConsulta(reporte.getFechaProceso())
                .porcentajeActasContabilizadas(Objects.isNull(reporte.getPorcentaje()) ? StringUtils.EMPTY
                        : reporte.getPorcentaje().toString())
                .tipoReporte(valores.getTipoReporte() != null ? valores.getTipoReporte() : "")
                .tipoEleccion(valores.getTipoEleccion() != null ? valores.getTipoEleccion() : "")
                .ambitoGeografico(valores.getAmbitoGeografico() != null ? valores.getAmbitoGeografico() : "")
                .ubigeoNivel1(valores.getUbigeoNivel1() != null ? valores.getUbigeoNivel1() : "")
                .ubigeoNivel2(valores.getUbigeoNivel2() != null ? valores.getUbigeoNivel2() : "")
                .ubigeoNivel3(valores.getUbigeoNivel3() != null ? valores.getUbigeoNivel3() : "")
                .localVotacion(valores.getLocalVotacion() != null ? valores.getLocalVotacion() : "")
                .estado(reporte.getEstado())
                .estadoDescripcion(EstadosReporteEnum.obtenerDescripcion(reporte.getEstado()))
                .idArchivo(reporte.getArchivo() != null? reporte.getArchivo().getId() : "")
                .build();
    }

}
