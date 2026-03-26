package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.presentacionbackend.model.bd.repository.secondary.TabReporteRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.ReporteConsultaOPService;
import pe.gob.onpe.presentacionbackend.model.dto.reporte.*;
import pe.gob.onpe.presentacionbackend.utils.enums.EstadosReporteEnum;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import java.util.*;
import java.util.stream.IntStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteConsultaOpServiceImpl implements ReporteConsultaOPService {


    private final TabReporteRepository tabReporteRepository;

    @Override
    public ReporteHistorialPaginado listarReportesAutomaticos(ReporteAutomaticoRequestDto request, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        Integer estado = 2;
        Integer activo = 1;

        Page<TabReporte> objetoTabReporte = null;
        if (Objects.isNull(request.getTipoEleccion())) {
            objetoTabReporte = tabReporteRepository
                    .findAllByCodigoUsuarioOrderByFechaCreacionDesc(
                            request.getUsuarioConsulta(),
                            pageable);

        } else {
            objetoTabReporte = tabReporteRepository
                    .findByCodigoUsuarioAndTipoEleccionInFiltroValoresOrderByFechaCreacionDesc(
                            request.getUsuarioConsulta(),
                            TipoEleccionEnum.obtenerDescripcionAlternativa(request.getTipoEleccion()),
                            estado,
                            activo,
                            pageable);

        }

        if (objetoTabReporte.isEmpty()) {
            log.info("No existen registros de reportes automaticos");
            return null;
        }

        return contruirRespuesta(objetoTabReporte);

    }

    private ReporteHistorialPaginado contruirRespuesta(Page<TabReporte> objetoTabReporte) {
        int paginaActual = objetoTabReporte.getNumber();
        int tamanioPagina = objetoTabReporte.getSize();
        int numeroInicio = paginaActual * tamanioPagina + 1;

        List<ReporteHistorialDto> registros = IntStream.range(0, objetoTabReporte.getContent().size())
                .mapToObj(i -> {
                    ReporteHistorialDto dto = mapperReporte(objetoTabReporte.getContent().get(i));
                    dto.setNumeroRegistro(numeroInicio + i);
                    return dto;
                })
                .sorted(Comparator.comparing(
                        ReporteHistorialDto::getFechaConsulta,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        return ReporteHistorialPaginado.builder()
                .paginaActual(objetoTabReporte.getNumber())
                .totalPaginas(objetoTabReporte.getTotalPages())
                .totalRegistros(objetoTabReporte.getTotalElements())
                .content(registros)
                .build();
    }

    private static ReporteHistorialDto mapperReporte(TabReporte reporte) {
        return ReporteHistorialDto.builder()
                .fechaConsulta(reporte.getFechaUltimaActualizacion())
                .estadoDescripcion(EstadosReporteEnum.obtenerDescripcion(reporte.getEstado()))
                .porcentaje(reporte.getPorcentaje())
                .idArchivo(reporte.getArchivo() != null? reporte.getArchivo().getId() : "")
                .build();
    }
    
}