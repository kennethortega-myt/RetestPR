package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.presentacionbackend.exception.NotFoundException;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.presentacionbackend.model.bd.service.ReporteConsultaOPService;
import pe.gob.onpe.presentacionbackend.model.bd.service.TabArchivoReporteService;
import pe.gob.onpe.presentacionbackend.model.dto.reporte.ReporteAutomaticoRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.reporte.ReporteHistorialPaginado;
import pe.gob.onpe.presentacionbackend.model.dto.response.*;
import pe.gob.onpe.presentacionbackend.nfs.FileApp;
import pe.gob.onpe.presentacionbackend.nfs.NfsService;
import pe.gob.onpe.presentacionbackend.utils.ArchivoUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/reportes")
@Slf4j
public class ReporteController {

	private final MongoOperations primaryMongo;
	private final MongoOperations secondaryMongo;
	private final ReporteConsultaOPService reporteConsultaOPService;
	private final TabArchivoReporteService tabArchivoReporteService;
	private final NfsService nfsService;

	private static final String N_ACTIVO = "n_activo";
	private static final String C_NOMBRE = "c_nombre";
	private static final String C_CODIGO = "c_codigo";
	private static final String N_ELECCION = "n_eleccion";
	private static final String ACTA_INFORMACION = "actaInformacion";
	private static final String FECHA_ACTA = "fechaActa";

	public ReporteController (
		MongoOperations primaryMongo,
		@Qualifier("secondaryMongoTemplate") MongoOperations secondaryMongo,
		ReporteConsultaOPService reporteConsultaOPService,
		TabArchivoReporteService tabArchivoReporteService,
		NfsService nfsService
	){
		this.primaryMongo = primaryMongo;
        this.secondaryMongo = secondaryMongo;
		this.reporteConsultaOPService = reporteConsultaOPService;
        this.tabArchivoReporteService = tabArchivoReporteService;
        this.nfsService = nfsService;
	}

	@GetMapping("/detalle-tipo-eleccion/{id}")
	public List<ReporteResponse> getDetailsByTypeElection(@PathVariable("id") Long idProceso) {

		Aggregation aggregation = Aggregation.newAggregation(
        	Aggregation.match(
            	Criteria.where("o_proceso_electoral.$id").is(idProceso)
                	.and(N_ACTIVO).is(1)
        		),
        	Aggregation.project(C_NOMBRE, C_CODIGO)
                   .andExclude("_id"),
        	Aggregation.sort(Sort.Direction.ASC, C_CODIGO)
    	);
		AggregationResults<TiposEleccionResponse> results = primaryMongo.aggregate(
            aggregation,
            "mae_eleccion",
            TiposEleccionResponse.class
        );
		List<TiposEleccionResponse> originalTipoEleccionesList = results.getMappedResults();
		List<TiposEleccionResponse> mappedTipoEleccionesList = this.typeElectionNameMapper(originalTipoEleccionesList);
		List<ReporteResponse> list = new ArrayList<>();

		Aggregation aggregation2 = Aggregation.newAggregation(
			Aggregation.match(
				Criteria.where(N_ACTIVO).is(1)
					.and(N_ELECCION).ne(0)
			),
			Aggregation.sort(Sort.Direction.ASC, N_ELECCION),
			Aggregation.project()
				.and(N_ELECCION).as("codigoTipoEleccion")
				.and(C_NOMBRE).as("nombreTipoEleccion")
				.and("c_icono").as("iconoTipoEleccion")
				.andExclude("_id")
		);
		AggregationResults<IconosTipoEleccionResponse> results2 = primaryMongo.aggregate(
			aggregation2,
				"mae_modulo",
				IconosTipoEleccionResponse.class
			);
		List<IconosTipoEleccionResponse> iconsByTypeElectionTypeList = results2.getMappedResults();

		Aggregation aggregationReportes = Aggregation.newAggregation(
				Aggregation.match(Criteria.where(N_ACTIVO).is(1)),
				Aggregation.project("_id", "c_ruta")
		);
		AggregationResults<TabReporteCandidato> resultsReportes = primaryMongo.aggregate(
				aggregationReportes,
				"tab_reporte_candidato",
				TabReporteCandidato.class
		);
		List<TabReporteCandidato> reportesActivos = resultsReportes.getMappedResults();

		for (TiposEleccionResponse tiposEleccionResponse : mappedTipoEleccionesList) {
			ReporteResponse item = new ReporteResponse();
			item.setNombreTipoEleccion(tiposEleccionResponse.getNombreTipoEleccion());
			item.setCodigoTipoEleccion(tiposEleccionResponse.getCodigoTipoEleccion());
			item.setIconoTipoEleccion(
				this.getIconForElectionType(
					tiposEleccionResponse.getCodigoTipoEleccion(),
					iconsByTypeElectionTypeList
				)
			);
			Long total = this.getTotalByElectionType(tiposEleccionResponse.getCodigoTipoEleccion()).getTotal();
			item.setTotalesPorTipoEleccion(total);
			List<ActaReporteResponse> certificates = this.getCertificatesByElectionType(tiposEleccionResponse.getCodigoTipoEleccion());
			item.setActas(certificates);
			item.setReporteDescarga(getReportPath(reportesActivos, tiposEleccionResponse.getCodigoTipoEleccion()));
			list.add(item);
		}
		return list;
	}

	@PostMapping("/automatico-paginado")
	public ResponseEntity<GenericResponse<ReporteHistorialPaginado>> listarReportesAutomaticoPublico(
			@RequestBody ReporteAutomaticoRequestDto request,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "10") int tamanio
	) {
		GenericResponse<ReporteHistorialPaginado> genericResponse = new GenericResponse<>();

		ReporteHistorialPaginado reporteHistorial = 
				reporteConsultaOPService.listarReportesAutomaticos(
						request,
						pagina,
						tamanio
				);

		if (reporteHistorial == null) {
			return ResponseEntity.noContent().build();
		} else{
			IconosTipoEleccionResponse iconElectionType = this.getIconForElectionType(
				request.getTipoEleccion()
			);
			reporteHistorial.setNombreTipoEleccion(this.typeElectionNameMapper(iconElectionType.getCodigoTipoEleccion()));
			reporteHistorial.setIconoTipoEleccion(iconElectionType.getIconoTipoEleccion());
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(reporteHistorial);
	
			return ResponseEntity.ok(genericResponse);
		}

	}

	@GetMapping("/file")
	public ResponseEntity<byte[]> getFile(@RequestParam("id") String idArchivo) throws  Exception{
		try {
			Optional<TabArchivo> oTabArchivo = this.tabArchivoReporteService.getArchivoById(idArchivo);
			if (oTabArchivo.isEmpty()) {
				throw new NotFoundException("File not found");
			}
			TabArchivo tabArchivo = oTabArchivo.get();
			String[] pathSegments = tabArchivo.getCRuta().split("/");
			String reportsFolder =  pathSegments[pathSegments.length-3];
			String moduleFolder = pathSegments[pathSegments.length-2];
			String pathForFileToDownload = reportsFolder + File.separator + moduleFolder + File.separator + tabArchivo.getCNombreOriginal();
			FileApp file = this.nfsService.download(pathForFileToDownload);
			String contentType = tabArchivo.getCFormato();
			return ResponseEntity.ok()
					.header("Content-Disposition", "attachment; filename=\"" + tabArchivo.getCNombreOriginal() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
					.body(file.getFile());
		} catch (NotFoundException nfe) {
			throw new NotFoundException("Archivo no encontrado " + nfe.getMessage());
		}
	}

	@GetMapping("/reporteCandidato")
	public ResponseEntity<?> getFileReporteCandidato(@RequestParam("id") Integer codigoTipoEleccion) throws Exception {

		TabReporteCandidato reporte = tabArchivoReporteService
				.findByIdAndActivo(codigoTipoEleccion, 1)
				.orElse(null);

		if (reporte == null || reporte.getCRuta() == null || reporte.getCRuta().isBlank()) {
			return ResponseEntity.noContent().build();
		}

		Path path = Paths.get(reporte.getCRuta());
		String fileName = path.getFileName().toString();

		if (!fileName.toLowerCase().endsWith(".csv")) {
			return ResponseEntity.noContent().build();
		}

		String moduleFolder = path.getParent().getFileName().toString();
		String pathForFileToDownload = moduleFolder + File.separator + fileName;

		FileApp file = nfsService.download(pathForFileToDownload);

		if (file == null || file.getFile() == null || file.getFile().length == 0) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + fileName + "\"")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(file.getFile());
	}

	private List<ActaReporteResponse> getCertificatesByElectionType (String codigoTipoEleccion){
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(
				Criteria.where(C_CODIGO).is("automatico")
                	.and("c_filtro").regex("\"tipoEleccion\":"+ codigoTipoEleccion +"")
			),
			Aggregation.lookup(
				"tab_archivo",
				"o_archivo.$id",
				"_id",
					ACTA_INFORMACION
			),
			Aggregation.unwind(ACTA_INFORMACION),
			Aggregation.project()
				.and("actaInformacion._id").as("idActa")
				.and("d_fecha_ultima_actualizacion").as(FECHA_ACTA)
				.and("n_porcentaje").as("porcentaje")
				.and("actaInformacion.c_ruta").as("rutaActa")
				.and("n_estado").as("estadoActa"),
			Aggregation.sort(Sort.Direction.DESC, FECHA_ACTA),
			Aggregation.limit(3)
		);
		AggregationResults<ActaReporteResponse> results = secondaryMongo.aggregate(
			aggregation,
			"tab_reporte",
			ActaReporteResponse.class
		);
		return results.getMappedResults();
	}

	private TotalReporteResponse getTotalByElectionType (String codigoTipoEleccion){
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(
				Criteria.where(C_CODIGO).is("automatico")
                	.and("c_filtro").regex("\"tipoEleccion\":"+ codigoTipoEleccion +"")
			),
			Aggregation.lookup(
				"tab_archivo",
				"o_archivo.$id",
				"_id",
					ACTA_INFORMACION
			),
			Aggregation.unwind(ACTA_INFORMACION),
			Aggregation.project()
				.and("actaInformacion._id").as("idActa")
				.and("actaInformacion.d_aud_fecha_creacion").as(FECHA_ACTA)
				.and("actaInformacion.c_ruta").as("rutaActa")
				.and("n_estado").as("estadoActa"),
			Aggregation.count().as("total")
		);
		AggregationResults<TotalReporteResponse> results = secondaryMongo.aggregate(aggregation, "tab_reporte", TotalReporteResponse.class);
		List<TotalReporteResponse> list = results.getMappedResults();
		if (list.isEmpty()) {
			TotalReporteResponse empty = new TotalReporteResponse();
			empty.setTotal(0L);
			return empty;
		}
		return list.get(0);
	}

	private String getIconForElectionType(String codigoTipoEleccion, List<IconosTipoEleccionResponse> iconsByTypeElectionTypeList) {
		for (IconosTipoEleccionResponse icono : iconsByTypeElectionTypeList) {
			if (icono.getCodigoTipoEleccion().equals(codigoTipoEleccion)) {
				return icono.getIconoTipoEleccion();
			}
		}
		return "";
	}

	private String getReportPath(List<TabReporteCandidato> reportesActivos, String codigoTipoEleccion) {
		return reportesActivos.stream()
				.filter(r -> r.getId().toString().equals(codigoTipoEleccion))
				.map(TabReporteCandidato::getCRuta)
				.findFirst()
				.orElse("");
	}

	private List<TiposEleccionResponse> typeElectionNameMapper(List<TiposEleccionResponse> originalElectionTypes){
		List<TiposEleccionResponse> tempList = new ArrayList<>();
		for (TiposEleccionResponse electionType : originalElectionTypes) {
			String tempName = "";
			switch (electionType.getCodigoTipoEleccion()) {
				case "10":
					tempName = "Presidencial";
					break;
				case "12":
					tempName = "Parlamento Andino";
					break;
				case "13":
					tempName = "Diputados";
					break;
				case "14":
					tempName = "Senadores Distrito Electoral Múltiple";
					break;
				case "15":
					tempName = "Senadores Distrito Electoral Único";
					break;
				default:
					break;
			}
			electionType.setNombreTipoEleccion(tempName);
			tempList.add(electionType);
		}
		return tempList;
	}

	private String typeElectionNameMapper(String codigoTipoEleccion){
		String tempName = "";
		switch (codigoTipoEleccion) {
			case "10":
				tempName = "Presidencial";
				break;
			case "12":
				tempName = "Parlamento Andino";
				break;
			case "13":
				tempName = "Diputados";
				break;
			case "14":
				tempName = "Senadores Distrito Electoral Múltiple";
				break;
			case "15":
				tempName = "Senadores Distrito Electoral Único";
				break;
			default:
				break;
		}
		return tempName;
	}

	private IconosTipoEleccionResponse getIconForElectionType(Integer codigoEleccion){
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(
				Criteria.where(N_ACTIVO).is(1).and(N_ELECCION).is(codigoEleccion)
			),
			Aggregation.project()
				.and(C_NOMBRE).as("nombreTipoEleccion")
				.and(N_ELECCION).as("codigoTipoEleccion")
				.and("c_icono").as("iconoTipoEleccion")
				.andExclude("_id")
		);
		AggregationResults<IconosTipoEleccionResponse> results = primaryMongo.aggregate(
			aggregation,
			"mae_modulo",
			IconosTipoEleccionResponse.class
		);
		List<IconosTipoEleccionResponse> iconsByTypeElectionTypeList = results.getMappedResults();
		if(iconsByTypeElectionTypeList.isEmpty()){
			IconosTipoEleccionResponse empty = new IconosTipoEleccionResponse();
			empty.setNombreTipoEleccion("");
			empty.setCodigoTipoEleccion("");
			empty.setIconoTipoEleccion("");
			return empty;
		}
		return iconsByTypeElectionTypeList.get(0);
	}
}
