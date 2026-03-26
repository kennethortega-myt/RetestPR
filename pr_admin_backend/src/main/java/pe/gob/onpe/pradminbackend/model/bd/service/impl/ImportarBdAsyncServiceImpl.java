package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.service.*;
import pe.gob.onpe.pradminbackend.model.dto.bd.importar.*;
import pe.gob.onpe.pradminbackend.model.dto.mapper.UtilMapper;
import pe.gob.onpe.pradminbackend.model.dto.request.ImportarRequest;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoProcesoElectoral;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportarBdAsyncServiceImpl {

    private static final int BATCH_SIZE = 1000;

    public static final String BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS = "/topic/update-progress-with-details";
    private static final String ATRIBUTOGETBD = "getBd";
    private static final String ATRIBUTOGETVISTAELECCION = "getVistaEleccion";
	public static final String SE_EJECUTO_EL_METODO_SAVE = "Se ejecutó el método save";

    private final ObjectMapper objectMapper;
	
    private final BroadcastWebsocketServiceImpl broadcastWebsocketService;
    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final CabCatalogoService cabCatalogoService;
    private final DetCatalogoEstructuraService detCatalogoEstructuraService;
    private final DetCatalogoReferenciaService detCatalogoReferenciaService;
    private final DetUbigeoEleccionAgrupacionPoliticaService detUbigeoEleccionAgrupacionPoliticaService;
    private final DetUbigeoEleccionService detUbigeoEleccionService;
    private final MaeAgrupacionPoliticaService agrupacionPoliticaService;
    private final MaeUbigeoService maeUbigeoService;
    private final MaeLocalVotacionService maeLocalVotacionService;
    private final MaeDistritoElectoralService maeDistritoElectoralService;
    private final MaeCandidatoService maeCandidatoService;
    private final MaeEleccionService maeEleccionService;
    private final MaeModuloService maeModuloService;
    private final ActaService actaService;

    private final ParticipacionCiudadanaService participacionCiudadanaService;
    private final VwPrMesaService vwPrMesaService;
    private final VwPrPresidencialesService vwPrPresidencialesService;
    private final VwPrDiputadosService vwPrDiputadosService;
    private final VwPrParlamentoAndinoService vwPrParlamentoAndinoService;
    private final VwPrSenadoresDistritoElectoralMultipleService vwPrSenadoresDistritoElectoralMultipleService;
    private final VwPrSenadoresDistritoNacionalUnicoService vwPrSenadoresDistritoNacionalUnicoService;
    private final VwPrRevocatoriaDistritalService vwPrRevocatoriaDistritalService;
    private final VwPrTotalCandidatosPorAgrupacionPoliticaService vwPrTotalCandidatosPorAgrupacionPoliticaService;

    private final MaeFechaService maeFechaService;
    private final MaeImportarService maeImportarService;
	
	private volatile boolean procesoConError = false;
    private int total = 0;
    private int current = 0;

	public boolean isBdValida() {
		log.debug("Verificando si la base de datos es válida...");
		Optional<MaeImportar> resultado = maeImportarService.getId(1);
		boolean esValida = resultado.map(MaeImportar::isExito).orElse(false);
		log.info("Resultado de validación de BD: {}", esValida ? "Válida" : "No válida");
		return esValida;
	}

	@Async
	public CompletableFuture<Void> migrar(ImportarDto request, ImportarRequest impRequest) {
		log.info("========== INICIO DEL PROCESO DE MIGRACIÓN ==========");
		log.info("Proceso Electoral: {}", request.getProceso() != null ? request.getProceso().size() : 0);
		try {
			this.total = calcularTotalRounds(request, impRequest);
			this.current = 0;
			log.info("Total de rounds calculados: {}", this.total);

			// ======================
			// BLOQUE BD
			// ======================
			log.info("---------- Iniciando BLOQUE BD ----------");
			migrarProcesoElectoral(request);
			migrarCatalogo(request);
			migrarCatalogoReferencia(request);
			migrarCatalogoEstructura(request);
			migrarUbigeoEleccionAgrupacionPolitica(request);
			migrarUbigeoEleccion(request);
			migrarAgrupacionPolitica(request);
			migrarUbigeo(request);
			migrarLocalVotacion(request);
			migrarDistritoElectoral(request, impRequest);
			migrarCandidato(request);
			marcarMaeImportValid(ATRIBUTOGETBD, request.getProceso());
			log.info("---------- Finalizado BLOQUE BD ----------");

			// ======================
			// BLOQUE VISTA ELECCION
			// ======================
			log.info("---------- Iniciando BLOQUE VISTA ELECCION ----------");
			procesarEleccionesYModulos(request);
			log.info("---------- Finalizado BLOQUE VISTA ELECCION ----------");

			// ======================
			// BLOQUE OTRAS VISTA
			// ======================
			log.info("---------- Iniciando BLOQUE OTRAS VISTAS ----------");
			migrarVistaActa(request);
			migrarVistaParticipacion(request);
			migrarVistaMesa(request);
			migrarVistaTotalCandidatos(request);
			log.info("---------- Finalizado BLOQUE OTRAS VISTAS ----------");

			if (!procesoConError) {
				log.info("Proceso completado sin errores. Actualizando fecha de migración...");
				actualizarFechaMigracion();
				finalizarProceso();
				log.info("========== PROCESO DE MIGRACIÓN FINALIZADO EXITOSAMENTE ==========");
			} else {
				log.debug("========== PROCESO DE MIGRACIÓN FINALIZADO CON ERRORES ==========");
			}
		} catch (Exception e) {
			log.debug("========== ERROR CRÍTICO EN PROCESO DE MIGRACIÓN ==========");
			log.debug("Tipo de error: {}", e.getClass().getName());
			log.debug("Mensaje de error: {}", e.getMessage());
			log.debug("Stack trace completo:", e);
			reportProgressImportar(
					getPercent(false),
					"Error durante la migración. Revise los logs.",
					PrConstantes.ESTADO_PROGRESO_FINALIZA_ERROR
			);
		}
		return CompletableFuture.completedFuture(null);
	}

	private int calcularTotalRounds(ImportarDto request, ImportarRequest impRequest) {
		int totalRounds = 0;

		if (impRequest.getGetMenu() != null && impRequest.getGetMenu() != 0) {
			totalRounds++;
		}

		totalRounds += calcularRounds(request.getProceso());
		totalRounds += calcularRounds(request.getCatalogo());
		totalRounds += calcularRounds(request.getCatalogoReferencia());
		totalRounds += calcularRounds(request.getCatalogoEstructura());
		totalRounds += calcularRounds(request.getUbigeoEleccionAgrupacionPolitica());
		totalRounds += calcularRounds(request.getUbigeoEleccion());
		totalRounds += calcularRounds(request.getAgrupacionPolitica());
		totalRounds += calcularRounds(request.getUbigeo());
		totalRounds += calcularRounds(request.getLocalVotacion());
		totalRounds += calcularRounds(request.getDistritoElectoral());
		totalRounds += calcularRounds(request.getCandidato());
		totalRounds += calcularRounds(request.getVistaActa());
		totalRounds += calcularRounds(request.getVistaParticipacionCiudadano());
		totalRounds += calcularRounds(request.getVistaMesa());
		totalRounds += calcularRounds(request.getVistaTotalCandidatosPorAgrupacionPoliticaExportDto());
		totalRounds += calcularRounds(request.getEleccion());

		if (request.getVistasEleccion() != null) {
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrPresidenciales());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrCongresales());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrDiputados());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrParlamentoAndino());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrSenadoresDistritoElectoralMultiple());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrSenadoresDistritoNacionalUnico());
			totalRounds += calcularRounds(request.getVistasEleccion().getVwPrRevocatoriaDistrital());
		}

		return totalRounds;
	}

	private int calcularRounds(List<?> lista) {
		if (lista == null || lista.isEmpty()) return 0;
		return (int) Math.ceil(Math.max(1, lista.size()) / (double) BATCH_SIZE);
	}

	private void migrarProcesoElectoral(ImportarDto request) {
		log.info("Migrando Proceso Electoral - Registros a procesar: {}", 
			request.getProceso() != null ? request.getProceso().size() : 0);
		migrarBatch(
			request.getProceso(),
			maeProcesoElectoralService::deleteAll,
			UtilMapper::convertirProcesoElectoral,
			maeProcesoElectoralService::saveAll,
			"Se inició la migración de proceso electoral"
		);
		log.info("Proceso Electoral migrado exitosamente");
	}

	private void migrarCatalogo(ImportarDto request) {
		log.info("Migrando Catálogo - Registros a procesar: {}", 
			request.getCatalogo() != null ? request.getCatalogo().size() : 0);
		migrarBatch(
			request.getCatalogo(),
			cabCatalogoService::deleteAll,
			UtilMapper::convertirCabCatalogo,
			cabCatalogoService::saveAll,
			"Se inició la migración del catálogo del proceso"
		);
		log.info("Catálogo migrado exitosamente");
	}

	private void migrarCatalogoReferencia(ImportarDto request) {
		log.info("Migrando Catálogo Referencia - Registros a procesar: {}", 
			request.getCatalogoReferencia() != null ? request.getCatalogoReferencia().size() : 0);
        migrarBatch(request.getCatalogoReferencia(),
			detCatalogoReferenciaService::deleteAll,
			UtilMapper::convertirDetCatalogoReferencia,
			detCatalogoReferenciaService::saveAll,
			"Se inició la migración del catálogo de referencia");
		log.info("Catálogo Referencia migrado exitosamente");
	}

	private void migrarCatalogoEstructura(ImportarDto request) {
		log.info("Migrando Catálogo Estructura - Registros a procesar: {}", 
			request.getCatalogoEstructura() != null ? request.getCatalogoEstructura().size() : 0);
        migrarBatch(request.getCatalogoEstructura(),
			detCatalogoEstructuraService::deleteAll,
			UtilMapper::convertirDetCatalogoEstructura,
			detCatalogoEstructuraService::saveAll,
			"Se inició la migración del catálogo de estructura");
		log.info("Catálogo Estructura migrado exitosamente");
	}

	private void migrarUbigeoEleccionAgrupacionPolitica(ImportarDto request) {
		log.info("Migrando Ubigeo Elección Agrupación Política - Registros a procesar: {}", 
			request.getUbigeoEleccionAgrupacionPolitica() != null ? request.getUbigeoEleccionAgrupacionPolitica().size() : 0);
        migrarBatch(request.getUbigeoEleccionAgrupacionPolitica(),
			detUbigeoEleccionAgrupacionPoliticaService::deleteAll,
			UtilMapper::convertirUbigeoEleccionAgrupacionPolitica,
			detUbigeoEleccionAgrupacionPoliticaService::saveAll,
			"Se inició la migración de elecciones por agrupación política");
		log.info("Ubigeo Elección Agrupación Política migrado exitosamente");
	}

	private void migrarUbigeoEleccion(ImportarDto request) {
		log.info("Migrando Ubigeo Elección - Registros a procesar: {}", 
			request.getUbigeoEleccion() != null ? request.getUbigeoEleccion().size() : 0);
        migrarBatch(request.getUbigeoEleccion(),
			detUbigeoEleccionService::deleteAll,
			UtilMapper::convertirUbigeoEleccion,
			detUbigeoEleccionService::saveAll,
			"Se inició la migración de ubigeo por elección");
		log.info("Ubigeo Elección migrado exitosamente");
	}

	private void migrarAgrupacionPolitica(ImportarDto request) {
		log.info("Migrando Agrupación Política - Registros a procesar: {}", 
			request.getAgrupacionPolitica() != null ? request.getAgrupacionPolitica().size() : 0);
        migrarBatch(request.getAgrupacionPolitica(),
			agrupacionPoliticaService::deleteAll,
			UtilMapper::convertirAgrupacionPolitica,
			agrupacionPoliticaService::saveAll,
			"Se inició la migración de agrupaciones políticas");
		log.info("Agrupación Política migrada exitosamente");
	}

	private void migrarUbigeo(ImportarDto request) {
		log.info("Migrando Ubigeo - Registros a procesar: {}", 
			request.getUbigeo() != null ? request.getUbigeo().size() : 0);
		migrarBatch(request.getUbigeo(),
			maeUbigeoService::deleteAll,
			UtilMapper::convertirUbigeo,
			maeUbigeoService::saveAll,
			"Se inició la migración de ubigeos"
		);
		log.info("Ubigeo migrado exitosamente");
	}

	private void migrarLocalVotacion(ImportarDto request) {
		log.info("Migrando Local Votación - Registros a procesar: {}", 
			request.getLocalVotacion() != null ? request.getLocalVotacion().size() : 0);
        migrarBatch(request.getLocalVotacion(),
                maeLocalVotacionService::deleteAll,
                UtilMapper::convertirLocalVotacion,
                maeLocalVotacionService::saveAll,
                "Se inició la migración de locales de votación");
		log.info("Local Votación migrado exitosamente");
	}

    private void migrarDistritoElectoral(ImportarDto request, ImportarRequest impRequest) {
        if (request.getDistritoElectoral() != null && !request.getDistritoElectoral().isEmpty()) {
			log.info("Migrando Distrito Electoral - Registros a procesar: {}", request.getDistritoElectoral().size());
            migrarBatch(
				request.getDistritoElectoral(),
				maeDistritoElectoralService::deleteAll,
				UtilMapper::convertirDistritoElectoral,
				maeDistritoElectoralService::saveAll,
				"Se inició la migración de distrito electoral"
            );
			log.info("Distrito Electoral migrado exitosamente");
        } else if (impRequest.getGetBd() == 1) {
			log.info("Distrito Electoral no participa en esta elección - omitiendo migración");
            reportProgressImportar(getPercent(false),
				"Distrito electoral no participa en esta elección",
				PrConstantes.ESTADO_PROGRESO_CONTINUA);
        }
    }

    private void migrarCandidato(ImportarDto request) {
		log.info("Migrando Candidato - Registros a procesar: {}", 
			request.getCandidato() != null ? request.getCandidato().size() : 0);
        migrarBatch(
			request.getCandidato(),
			maeCandidatoService::deleteAll,
			UtilMapper::convertirCandidato,
			maeCandidatoService::saveAll,
			"Se inició la migración de candidatos"
        );
		log.info("Candidato migrado exitosamente");
    }

	private void procesarEleccionesYModulos(ImportarDto request) {
		log.info("Procesando Elecciones y Módulos...");
		if (request.getEleccion() == null || request.getEleccion().isEmpty()) {
			log.warn("No hay elecciones para procesar");
			return;
		}

		log.info("Total de elecciones a procesar: {}", request.getEleccion().size());
		boolean existePrincipal = request.getEleccion()
				.stream()
				.anyMatch(e -> e.getPrincipal() == 1);
		log.debug("¿Existe elección principal?: {}", existePrincipal);

		if (!existePrincipal) {
			log.debug("ERROR DE VALIDACIÓN: No existe elección marcada como Principal");
			procesoConError = true;
			reportProgressImportar(
					getPercent(false),
					"Debe haber al menos una elección marcada como Principal en el sistema SCE para continuar.",
					PrConstantes.ESTADO_PROGRESO_FINALIZA_ERROR
			);
        	return;
		}
		log.info("Validación de elección principal exitosa");

		List<MaeEleccion> entities = request.getEleccion().stream()
			.map(UtilMapper::convertirEleccion)
			.toList();

		migrarBatchPostAction(
				request.getEleccion(),
				maeEleccionService::deleteAll,
				UtilMapper::convertirEleccion,
				maeEleccionService::saveAll,
				"Se inició la migración de elecciones",
            	() -> activarModulosPorEleccion(entities)

		);
		
		migrarVistasEleccion(request);
		marcarMaeImportValid(ATRIBUTOGETVISTAELECCION, request.getEleccion());
	}

	private void activarModulosPorEleccion(List<MaeEleccion> elecciones) {
		log.info("Activando módulos por elección - Elecciones: {}", elecciones.size());
		List<MaeModulo> modulos = maeModuloService.findAll();
		log.debug("Total de módulos encontrados: {}", modulos.size());
		
		List<MaeProcesoElectoral> procesosActivos = maeProcesoElectoralService.findAll()
			.stream()
			.filter(p -> p.getActivo() == 1)
			.toList();
		log.debug("Procesos electorales activos: {}", procesosActivos.size());

		if (modulos.isEmpty() || procesosActivos.isEmpty()) {
			log.warn("No se pueden activar módulos. Módulos vacío: {}, Procesos activos vacío: {}", 
				modulos.isEmpty(), procesosActivos.isEmpty());
			return;
		}

		MaeProcesoElectoral proceso = procesosActivos.get(0);
		log.debug("Proceso electoral obtenido: ID={}", proceso.getId());

		elecciones.forEach(eleccion -> {
			log.debug("Procesando elección: Código={}, Principal={}", 
				eleccion.getCodigo(), eleccion.getPrincipal());

			if (eleccion.getPrincipal() == 1) {
				log.info("Elección principal detectada - Código: {}", eleccion.getCodigo());

				List<MaeModulo> modulosAActivar = new ArrayList<>();

				if (Objects.equals(Long.parseLong(eleccion.getCodigo()),
						TipoEleccionEnum.PRESIDENCIAL.getCodigo())) {
					log.info("Configurando proceso como ELECCIONES_GENERAL");
					proceso.setTipoProcesoElectoral(TipoProcesoElectoral.ELECCIONES_GENERAL.getEtiqueta());
					configurarModulos(modulos, modulosAActivar, 1L, 51L, 52L);

				} else if (Objects.equals(Long.parseLong(eleccion.getCodigo()),
						TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo())) {
					log.info("Configurando proceso como REVOCATORIA");
					proceso.setTipoProcesoElectoral(TipoProcesoElectoral.REVOCATORIA.getEtiqueta());
					configurarModulos(modulos, modulosAActivar, 2L, 53L, 54L);
				}

				try {
					maeProcesoElectoralService.save(proceso);
					maeModuloService.saveAll(modulosAActivar);
					log.info("Proceso electoral y módulos guardados exitosamente - {} módulos activados", 
						modulosAActivar.size());
				} catch (Exception e) {
					log.debug("Error al guardar proceso electoral o módulos: {}", e.getMessage());
					log.debug("Stack trace:", e);
				}
			}

			modulos.stream().filter(m -> m.getEleccion() != null && m.getEleccion().equals(Integer.parseInt(eleccion.getCodigo())))
				.findFirst()
				.ifPresent(modulo -> {
					log.debug("Activando módulo para elección {} - Módulo ID: {}", eleccion.getCodigo(), modulo.getId());
					modulo.setActivo(1);
					modulo.setPrincipal(eleccion.getPrincipal() == 1);
					try {
						maeModuloService.save(modulo);
						log.debug("Módulo {} guardado exitosamente", modulo.getId());
					} catch (Exception e) {
						log.debug("Error al guardar módulo {}: {}", modulo.getId(), e.getMessage());
						log.debug("Stack trace:", e);
					}

					if (modulo.getPadre() > 0) {
						log.debug("Activando módulo padre ID: {}", modulo.getPadre());
						modulos.stream()
							.filter(p -> p.getId() == modulo.getPadre())
							.findFirst()
							.ifPresent(padre -> {
								padre.setActivo(1);
								try {
									maeModuloService.save(padre);
									log.debug("Módulo padre {} activado exitosamente", padre.getId());
								} catch (Exception e) {
									log.debug("Error al activar módulo padre {}: {}", padre.getId(), e.getMessage());
									log.debug("Stack trace:", e);
								}
							});
					}
				});
		});
		log.info("Activación de módulos por elección completada");
	}

	private void migrarVistasEleccion(ImportarDto request) {
		log.info("Migrando vistas de elección...");
		if (request.getVistasEleccion() == null) {
			log.warn("No hay vistas de elección para procesar");
			return;
		}

		migrarBatch(
			request.getVistasEleccion().getVwPrPresidenciales(),
			vwPrPresidencialesService::deleteAll,
			UtilMapper::convertirEleccionPresidencial,
			vwPrPresidencialesService::saveAll,
			"Se inició la migración de la vista elección presidenciales"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrCongresales(),
			vwPrDiputadosService::deleteAll,
			UtilMapper::convertirEleccionDiputados,
			vwPrDiputadosService::saveAll,
			"Se inició la migración de la vista elección congresales"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrDiputados(),
			vwPrDiputadosService::deleteAll,
			UtilMapper::convertirEleccionDiputados,
			vwPrDiputadosService::saveAll,
			"Se inició la migración de la vista elección diputados"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrParlamentoAndino(),
			vwPrParlamentoAndinoService::deleteAll,
			UtilMapper::convertirParlamentoAndino,
			vwPrParlamentoAndinoService::saveAll,
			"Se inició la migración de la vista parlamento andino"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrSenadoresDistritoElectoralMultiple(),
			vwPrSenadoresDistritoElectoralMultipleService::deleteAll,
			UtilMapper::convertirEleccionSenadoresDistritoElectoralMultiple,
			vwPrSenadoresDistritoElectoralMultipleService::saveAll,
			"Se inició la migración de la vista senadores distrito electoral múltiple"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrSenadoresDistritoNacionalUnico(),
			vwPrSenadoresDistritoNacionalUnicoService::deleteAll,
			UtilMapper::convertirSenadoresDistritoNacionalUnico,
			vwPrSenadoresDistritoNacionalUnicoService::saveAll,
			"Se inició la migración de la vista senadores distrito nacional único"
		);

		migrarBatch(
			request.getVistasEleccion().getVwPrRevocatoriaDistrital(),
			vwPrRevocatoriaDistritalService::deleteAll,
			UtilMapper::convertirRevocatoriaDistrital,
			vwPrRevocatoriaDistritalService::saveAll,
			"Se inició la migración de la vista revocatoria distrital"
		);
		log.info("Vistas de elección migradas exitosamente");
	}

	private void migrarVistaActa(ImportarDto request) {
		log.info("Migrando Vista Acta - Registros a procesar: {}", 
			request.getVistaActa() != null ? request.getVistaActa().size() : 0);
		List<MaeModulo> modulosActivos = maeModuloService.findAll()
			.stream()
			.filter(m -> m.getActivo() == 1 && m.getEleccion() > 0)
			.toList();
		log.debug("Módulos activos encontrados: {}", modulosActivos.size());

		migrarBatchPostAction(
			request.getVistaActa(),
			actaService::deleteAll,
			dto -> UtilMapper.convertirActa(dto, modulosActivos),
			actaService::saveAll,
			"Se inició la migración de la vista Actas",
            () -> marcarMaeImportValid("getVistaActa", request.getVistaActa())
		);
		log.info("Vista Acta migrada exitosamente");
	}

	private void migrarVistaParticipacion(ImportarDto request) {
		log.info("Migrando Vista Participación - Registros a procesar: {}", 
			request.getVistaParticipacionCiudadano() != null ? request.getVistaParticipacionCiudadano().size() : 0);
		migrarBatchPostAction(
			request.getVistaParticipacionCiudadano(),
			participacionCiudadanaService::deleteAll,
			UtilMapper::convertirParticipacionCiudadana,
			participacionCiudadanaService::saveAll,
			"Se inició la migración de la vista Participación Ciudadana",
            () -> marcarMaeImportValid("getVistaParticipacionCiudadana", request.getVistaParticipacionCiudadano())
		);
		log.info("Vista Participación migrada exitosamente");
	}

	private void migrarVistaMesa(ImportarDto request) {
		log.info("Migrando Vista Mesa - Registros a procesar: {}", 
			request.getVistaMesa() != null ? request.getVistaMesa().size() : 0);
		migrarBatchPostAction(
			request.getVistaMesa(),
			vwPrMesaService::deleteAll,
			UtilMapper::convertirVistaMesa,
			vwPrMesaService::saveAll,
			"Se inició la migración de la vista Mesas",
            () -> marcarMaeImportValid("getVistaMesa", request.getVistaMesa())
		);
		log.info("Vista Mesa migrada exitosamente");
	}

	private void migrarVistaTotalCandidatos(ImportarDto request) {
		log.info("Migrando Vista Total Candidatos - Registros a procesar: {}", 
			request.getVistaTotalCandidatosPorAgrupacionPoliticaExportDto() != null ? 
			request.getVistaTotalCandidatosPorAgrupacionPoliticaExportDto().size() : 0);
		migrarBatchPostAction(
			request.getVistaTotalCandidatosPorAgrupacionPoliticaExportDto(),
			vwPrTotalCandidatosPorAgrupacionPoliticaService::deleteAll,
			UtilMapper::convertirVistaTotalCandidatosPorAgrupacionPolitica,
			vwPrTotalCandidatosPorAgrupacionPoliticaService::saveAll,
			"Se inició la migración de vista total candidatos por agrupación política",
			() -> marcarMaeImportValid("getVistaTotalCandidatosPorAgrupacionPolitica", request.getVistaTotalCandidatosPorAgrupacionPoliticaExportDto())
		);
		log.info("Vista Total Candidatos migrada exitosamente");
	}

	private boolean tieneDatos(List<?> lista) {
		return lista != null && !lista.isEmpty();
	}

	private <D, E> void migrarBatch(
		List<D> source,
		Runnable deleteAction,
		Function<D, E> mapper,
		Consumer<List<E>> saveAllAction,
		String mensajeInicio
	) {
		if (!tieneDatos(source)) {
			log.debug("No hay datos para migrar en: {}", mensajeInicio);
			return;
		}

		log.info("{} - Total de registros: {}", mensajeInicio, source.size());
		try {
			log.debug("Eliminando datos anteriores...");
			deleteAction.run();
			log.debug("Datos anteriores eliminados exitosamente");
			reportProgressImportar(getPercent(false), mensajeInicio, PrConstantes.ESTADO_PROGRESO_CONTINUA);

			log.debug("Mapeando {} registros...", source.size());
			List<E> entities = source.stream()
					.map(mapper)
					.toList();
			log.debug("Registros mapeados exitosamente");

			int totalBatches = (int) Math.ceil(entities.size() / (double) BATCH_SIZE);
			log.info("Procesando {} registros en {} lotes de {} registros", 
				entities.size(), totalBatches, BATCH_SIZE);

			for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
				int end = Math.min(entities.size(), i + BATCH_SIZE);
				int batchNumber = (i / BATCH_SIZE) + 1;
				log.debug("Guardando lote {}/{} ({} registros)...", 
					batchNumber, totalBatches, end - i);
				
				try {
					saveAllAction.accept(entities.subList(i, end));
					log.debug("Lote {}/{} guardado exitosamente", batchNumber, totalBatches);
				} catch (Exception e) {
					log.debug("Error al guardar lote {}/{}: {}", batchNumber, totalBatches, e.getMessage());
					log.debug("Stack trace del error en lote:", e);
					throw e;
				}
				
				reportProgressImportar(getPercent(true), null, PrConstantes.ESTADO_PROGRESO_CONTINUA);
			}
			log.info("Migración completada exitosamente - Total procesado: {} registros", entities.size());
		} catch (Exception e) {
			log.debug("Error en migrarBatch para: {}", mensajeInicio);
			log.debug("Detalle del error: {}", e.getMessage());
			log.debug("Stack trace:", e);
			throw e;
		}
	}

	private <D, E> void migrarBatchPostAction(
		List<D> source,
		Runnable deleteAction,
		Function<D, E> mapper,
		Consumer<List<E>> saveAllAction,
		String mensajeInicio,
    	Runnable postAction
	) {
		if (!tieneDatos(source)) {
			log.debug("No hay datos para migrar en migrarBatchPostAction: {}", mensajeInicio);
			return;
		}

		try {
			migrarBatch(source, deleteAction, mapper, saveAllAction, mensajeInicio);

			if (postAction != null) {
				log.debug("Ejecutando acción posterior para: {}", mensajeInicio);
				postAction.run();
				log.debug("Acción posterior ejecutada exitosamente");
			}
		} catch (Exception e) {
			log.debug("Error en migrarBatchPostAction para: {}", mensajeInicio);
			log.debug("Detalle del error: {}", e.getMessage());
			log.debug("Stack trace:", e);
			throw e;
		}
	}

	public void reportProgressImportar(float percent, String message, String estado) {
        Map<String, Object> payloadImp = new HashMap<>();
        payloadImp.put("porcentaje", percent);
        payloadImp.put("texto", message);
        payloadImp.put("estado", estado);
        
        log.debug("Reportando progreso - Porcentaje: {}%, Estado: {}, Mensaje: {}", 
            percent, estado, message);
        
        try {
            String payload = this.objectMapper.writeValueAsString(payloadImp);
            log.trace("Payload WebSocket: {}", payload);
            this.broadcastWebsocketService.broadcastProgressUpdate(BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS, payload);
            Thread.sleep(100);
        } catch (JsonProcessingException e) {
            log.debug("Error al serializar payload de progreso: {}", e.getMessage());
            log.debug("Stack trace:", e);
        } catch (InterruptedException e) {
            log.debug("Interrupción al reportar progreso: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.debug("Error inesperado en reportProgressImportar: {}", e.getMessage());
            log.debug("Stack trace:", e);
        }
    }

    public int getPercent(boolean update) {
    	if(update) { this.current++; }
    	float porcentaje = (this.current * 100) / (float) this.total;
    	log.info("Avance del porcentaje: " + porcentaje);
        return Math.round(porcentaje);
    }

	private <D> void  marcarMaeImportValid(
		String atributo, 
		List<D> source
	) {
		if (!tieneDatos(source)) {
			log.debug("No se marca importación válida para '{}' - sin datos", atributo);
			return;
		}
		if (source == null || source.isEmpty()) {
			log.debug("No se marca importación válida para '{}' - fuente vacía o nula", atributo);
			return;
		}
		log.info("Marcando importación válida para atributo: {}", atributo);
		marcarMaeImport(atributo);
	}

	private void marcarMaeImport(
		String atributo
	) {
		log.debug("Buscando MaeImportar para atributo: {}", atributo);
		MaeImportar maeImportar = maeImportarService.getAtributo(atributo);
		if (maeImportar != null) {
			log.debug("MaeImportar encontrado, marcando como exitoso");
			maeImportar.setExito(true);
			maeImportar.setAudFechaModificacion(new Date());
			maeImportarService.save(maeImportar);
			log.info("Atributo '{}' marcado como exitoso en MaeImportar", atributo);
		} else {
			log.warn("No se encontró registro MaeImportar para atributo: {}", atributo);
		}
	}

	private void actualizarFechaMigracion() {
		log.info("Actualizando fecha de migración...");
		try {
			Optional<MaeFecha> fecha = maeFechaService.findById(1);
			if (fecha.isEmpty()) {
				log.info("Creando nuevo registro de fecha de migración");
				Date fechaRegistroMaeFecha = new Date();
				log.info("======ImportarBdAsyncServiceImpl: Esta es la fecha que se regsitra en maefecha {} =======", fechaRegistroMaeFecha);
				MaeFecha obj = MaeFecha.builder()
						.id(1)
						.activo(1)
						.audFechaCreacion(new Date())
						.servicioFirma(true)
						.audUsuarioCreacion("usuario-sce")
						.cDescripcion("Ultima Fecha Migración")
						.fechaProceso(fechaRegistroMaeFecha)
						.build();
				maeFechaService.save(obj);
				log.info("Fecha de migración creada exitosamente");
			} else {
				log.debug("Registro de fecha de migración ya existe");
			}
		} catch (Exception e) {
			log.debug("Error al actualizar fecha de migración: {}", e.getMessage());
			log.debug("Stack trace:", e);
		}
	}

	private void finalizarProceso() {
		log.info("Finalizando proceso de migración - Estado: EXITOSO");
		reportProgressImportar(
			100,
			"Finalizó la carga de datos",
			PrConstantes.ESTADO_PROGRESO_FINALIZA_OK
		);
	}
    
    private void configurarModulos(List<MaeModulo> finalLstMaeModulo, List<MaeModulo> modulosAActivar, Long idResumen, Long idParticipacion, Long idActa) {
		finalLstMaeModulo.forEach(modulo ->
				log.info("MaeModulo disponible: {}", modulo)
		);
		log.info("Configurando módulos - IDs: Resumen={}, Participación={}, Acta={}", 
			idResumen, idParticipacion, idActa);
        agregarModuloSiExiste(finalLstMaeModulo, modulosAActivar, idResumen);
        agregarModuloSiExiste(finalLstMaeModulo, modulosAActivar, idParticipacion);
        agregarModuloSiExiste(finalLstMaeModulo, modulosAActivar, idActa);
		log.info("Módulos configurados: {} módulos agregados para activar", modulosAActivar.size());
    }

    private void agregarModuloSiExiste(List<MaeModulo> finalLstMaeModulo, List<MaeModulo> modulosAActivar, Long idModulo) {
		log.info("Buscando módulo con ID: {}", idModulo);
        finalLstMaeModulo.stream()
                .filter(f -> f.getId().equals(idModulo))
                .findFirst()
                .ifPresent(modulo -> {
					log.info("Módulo ID={} encontrado y activado", idModulo);
                    modulo.setActivo(1);
                    modulosAActivar.add(modulo);
                });
    }
}
