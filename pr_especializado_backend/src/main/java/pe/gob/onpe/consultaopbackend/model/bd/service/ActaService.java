package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.consultaopbackend.model.dto.actas.*;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaCsvDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaDto;

import java.util.List;
import java.util.Optional;

public interface ActaService extends CrudService<VwPrActa> {
	ActaResponseDto obtenerActaPorId(Long id);
	Optional<ActaPaginaResponseDto> obtenerActas(ActaReqDto filtro, String codigoOp, int pagina, int tamanio);
	Optional<ActaPaginaResponseDto> obtenerActasObservadas(ActaRequestDto filtro, int pagina, int tamanio);
	List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro);
	List<ActaAgrupacionCandidatoRes> listarCandidatos(Long id, Long idAgru);


	ReporteRespuestaDto obtenerActasReporte(ActaRequestDto filtro);
	ReporteRespuestaCsvDto obtenerActasReporteSinArchivo(ActaRequestDto filtro);
	ReporteRespuestaDto obtenerActasReporteObservadas(ActaRequestDto filtro);
	ReporteRespuestaCsvDto obtenerActasReporteObservadasCsv(ActaRequestDto filtro);


	Optional<ResumenActasObservadasResDto> obtenerResumenActasObservadas(ResumenActasObservadasReqDto filtro);

}
