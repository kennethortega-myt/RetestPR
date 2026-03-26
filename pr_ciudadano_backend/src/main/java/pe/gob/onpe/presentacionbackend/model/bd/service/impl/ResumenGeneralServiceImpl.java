package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import pe.gob.onpe.presentacionbackend.model.bd.documents.*;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.*;
import pe.gob.onpe.presentacionbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.*;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class ResumenGeneralServiceImpl implements ResumenGeneralService {

	public static final String DISTRITO_ELECTORAL = "distrito_electoral";
	private final MaeResumenGeneralRepositoryCustom resumenGeneralRepositoryCustom;
	private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
	private final VwPrDiputadosRepository vwPrDiputadosRepository;
	private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
	private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
	private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
	private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;
	private final MaeFechaRepository maeFechaRepository;
	private final MaeEleccionRepository maeEleccionRepository;
	private final MaeUbigeoRepository maeUbigeoRepository;
	private final DetUbigeoEleccionRepository detUbigeoEleccionRepository;
	private final MongoOperations primaryMongo;


	@Override
	public Optional<ActaEleccionDto> obtenerTotalesPorEleccion(FiltroActaEleccionDto filtros) {
		if (filtros.getTipoFiltro().equals(DISTRITO_ELECTORAL) && filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) == 0) {
			filtros.setIdDistritoElectoral(15);
		}
		
		Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroActaEleccionDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
		Predicate<FiltroActaEleccionDto> tieneDistritoElectoral = data -> data.getIdDistritoElectoral()!= null && data.getIdDistritoElectoral() != 0;

		Optional<ActaEleccionDto> dto;
		List<VwPrPresidenciales> lstPresidencial;
		List<VwPrDiputados> lstDiputados;
		List<VwPrParlamentoAndino> lstParlamentoAndino;
		List<VwPrSenadoresDistritoElectoralMultiple> lstSenadores27;
		List<VwPrSenadoresDistritoNacionalUnico> lstSenadores33;
		List<VwPrRevocatoriaDistrital> lstRevocatoriaDistrital;
		switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
			case "Presidencial":
				lstPresidencial = obtenerResumenPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
				dto = optPresidencial(lstPresidencial);
				break;
			case "Diputados":
				lstDiputados = obtenerResumenDiputados(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
				dto = optDiputados(lstDiputados);
				break;
			case "Parlamento Andino":
				lstParlamentoAndino = obtenerResumenParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
				dto = optParlamentoAndino(lstParlamentoAndino);
				break;
			case "Senadores 27":
				lstSenadores27 = obtenerResumenSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
				dto = optSenadores27(lstSenadores27);
				break;
			case "Senadores 33":
				lstSenadores33 = obtenerResumenSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros);
				dto = optSenadores33(lstSenadores33);
				break;
			case "Revocatoria Distrital":
				lstRevocatoriaDistrital = obtenerRevocatoriaDistrital(tieneEleccionAndFiltro, tieneAmbito, tieneUbigeo1, tieneUbigeo2, tieneUbigeo3, filtros);
				dto = optRevocatoriaDistrital(lstRevocatoriaDistrital);
				break;
			default:
				dto = Optional.empty();
		}
		dto.ifPresent(this::calculaPorcentageTotalVotosEmitidosValidos);
		asignarUbigeoPorDefectoPorEleccion(dto,filtros.getIdEleccion());
		asignarFechaProceso(dto);
        return dto;
	}

	private void calculaPorcentageTotalVotosEmitidosValidos(ActaEleccionDto actaEleccionDto){
		if(actaEleccionDto.getTotalVotosEmitidos() != null){
			actaEleccionDto.setPorcentajeVotosEmitidos(actaEleccionDto.getTotalVotosEmitidos() == 0 ? 0 : 100);
			actaEleccionDto.setPorcentajeVotosValidos(actaEleccionDto.getTotalVotosEmitidos() == 0 ? 0 : 100);
		}else{
			actaEleccionDto.setPorcentajeVotosEmitidos(0);
			actaEleccionDto.setPorcentajeVotosValidos(0);
		}
	}

	private void asignarUbigeoPorDefectoPorEleccion(Optional<ActaEleccionDto> dto,Integer idEleccion){
		if(dto.isPresent()) {

			if(idEleccion == 7 ) { // revocatoria distrital

				// Obtener la lista completa
				List<DetUbigeoEleccion> ubigeosEleccion = detUbigeoEleccionRepository.findByEleccion(
						new MaeEleccion(Long.valueOf(idEleccion))
				);
				// Filtrar los elementos que tengan al menos 5 caracteres en el idUbigeo
				List<DetUbigeoEleccion> filtrados = ubigeosEleccion.stream()
						.filter(dato -> dato.getIdUbigeo().toString().length() >= 5)
						.toList();
				// Obtener uno aleatorio si la lista no está vacía
				if (!filtrados.isEmpty()) {
					SecureRandom secureRandom = new SecureRandom();
					int indexAleatorio = secureRandom.nextInt(filtrados.size());
					Long idUbigeoAleatorio = filtrados.get(indexAleatorio).getIdUbigeo();

					String idUbigeo = idUbigeoAleatorio.toString().trim();
					Map<Integer, String> niveles = generarNiveles(idUbigeo);

					dto.get().setIdUbigeoDepartamento(Long.parseLong(niveles.get(1)));
					dto.get().setIdUbigeoProvincia(Long.parseLong(niveles.get(2)));
					dto.get().setIdUbigeoDistrito(Long.parseLong(niveles.get(3)));
				} else {
					// Valores por defecto
					dto.get().setIdUbigeoDepartamento(250000L);
					dto.get().setIdUbigeoProvincia(250200L);
					dto.get().setIdUbigeoDistrito(250206L);
				}

			} else { // eleccion general
				dto.get().setIdUbigeoDepartamento(Long.parseLong("140000"));
				dto.get().setIdUbigeoProvincia(Long.parseLong("140100"));
				dto.get().setIdUbigeoDistrito(Long.parseLong("140101"));
				dto.get().setIdUbigeoDistritoElectoral(15);
			}
		}
	}

	private static Map<Integer,String> generarNiveles(String cadena) {
		// Asegurar que la cadena tenga 6 dígitos
		String padded = String.format("%6s", cadena).replace(' ', '0');

		Map<Integer,String> niveles = new HashMap<>();

		// Primer nivel: primeros 2 dígitos + "0000"
		String nivel1 = padded.substring(0, 2) + "0000";

		// Segundo nivel: primeros 4 dígitos + "00"
		String nivel2 = padded.substring(0, 4) + "00";

		// Tercer nivel: el número completo de 6 dígitos

        niveles.put(1,nivel1);
		niveles.put(2,nivel2);
		niveles.put(3, padded);

		return niveles;
	}

	private void asignarFechaProceso(Optional<ActaEleccionDto> actaEleccionDto){
		if(actaEleccionDto.isPresent()) {
			MaeFecha fechaProceso = maeFechaRepository.findById(1).orElse(MaeFecha.builder().fechaProceso(new Date()).build());
			actaEleccionDto.get().setFechaActualizacion(fechaProceso.getFechaProceso());
		}

	}
	
	private Optional<ActaEleccionDto> optPresidencial(List<VwPrPresidenciales> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
				  .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
				    .build()
				  );
	}
	
	private Optional<ActaEleccionDto> optDiputados(List<VwPrDiputados> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
			      .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
				    .build()
				  );
	}
	
	private Optional<ActaEleccionDto> optSenadores27(List<VwPrSenadoresDistritoElectoralMultiple> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
				  .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
				    .build()
				  );
	}
	
	private Optional<ActaEleccionDto> optParlamentoAndino(List<VwPrParlamentoAndino> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
			      .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
				    .build()
				  );
	}
	
	private Optional<ActaEleccionDto> optSenadores33(List<VwPrSenadoresDistritoNacionalUnico> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
				  .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
				    .build()
				  );
	}
	
	private Optional<ActaEleccionDto> optRevocatoriaDistrital(List<VwPrRevocatoriaDistrital> lstaEleccion){
		return Optional.ofNullable(lstaEleccion)
				  .filter(lista -> !lista.isEmpty())
				  .map(registro -> ActaEleccionDto.builder()
				    .actasContabilizadas(registro.get(0).getPorcentajeActasContabilizadas())
				    .contabilizadas(registro.get(0).getActasContabilizadas())
				    .totalActas(registro.get(0).getTotalActas())
				    .participacionCiudadana(registro.get(0).getPorcentajeParticipacionCiudadana())
				    .actasEnviadasJee(registro.get(0).getPorcentajeActasObservadasEnviadas())
				    .enviadasJee(registro.get(0).getActasObservadasEnviadas())
				    .actasPendientesJee(registro.get(0).getPorcentajeActasPendientes())
				    .pendientesJee(registro.get(0).getActasPendientes())
				    .fechaActualizacion(registro.get(0).getAudFechaModificacion())
				    .totalVotosEmitidos(registro.get(0).getTotalVotosEmitidos())
				    .totalVotosValidos(registro.get(0).getTotalVotosValidos())
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

		if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
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

		if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
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
	
	private Page<VwPrRevocatoriaDistrital> obtenerRevocatoriaDistritalPageable(
			Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro,
			 Predicate<FiltroActaEleccionDto> tieneAmbito,
			 Predicate<FiltroActaEleccionDto> tieneUbigeo1,
			 Predicate<FiltroActaEleccionDto> tieneUbigeo2,
			 Predicate<FiltroActaEleccionDto> tieneUbigeo3,
			 FiltroActaEleccionDto filtros,
			 Pageable pageable) {
		
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro(), pageable);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(), pageable);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(), pageable);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(), pageable);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			return this.vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getIdUbigeoDepartamento(),filtros.getIdUbigeoProvincia(),filtros.getIdUbigeoDistrito(), pageable);
		}
		return Page.empty();
	}

	@Override
	public List<ParticipanteDto> listarParticipantesPorEleccion(FiltroParticipanteDto filtros) {
		return this.resumenGeneralRepositoryCustom.listarParticipantesPorEleccion(filtros);
	}

	@Override
	public List<ActaMapaCalorResponseDto>  	listarMapaCalor(ActaMapaCalorRequestDto filtros, String actaCodigoEstado) {
		Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneDistritoElectoral = data -> data.getIdDistritoElectoral()!= null && data.getIdDistritoElectoral() != 0;


		return switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
			case "Presidencial" -> obtenerDataPresidencial(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);

			case "Diputados" -> obtenerDataDiputados(tieneEleccionAndFiltro,tieneDistritoElectoral,filtros,actaCodigoEstado);
			case "Parlamento Andino" ->obtenerDataParlamentoAndino(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
			case "Senadores 27" -> obtenerDataSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,filtros,actaCodigoEstado);
			case "Senadores 33" ->obtenerDataSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
			case "Revocatoria Distrital" -> obtenerDataRevocatoriaDistrital(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros,actaCodigoEstado);
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
																Predicate<ActaMapaCalorRequestDto> tieneDistritoElectoral,
																ActaMapaCalorRequestDto filtros,
																String actaCodigoEstado) {

		String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

		List<VwPrDiputados> registros = null;
		if(tieneEleccionAndFiltro.and(tieneDistritoElectoral.negate()).test(filtros)){
			registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
			return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
		} else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).test(filtros)){
			registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), tipoFiltro,filtros.getIdDistritoElectoral());
			return  construirRespuestaDiputados(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
		} else {
			return Collections.emptyList();
		}
	}
	
	private List<ActaMapaCalorResponseDto> obtenerDataSenadores27(Predicate<ActaMapaCalorRequestDto> tieneEleccionAndFiltro,
			Predicate<ActaMapaCalorRequestDto> tieneDistritoElectoral,
			ActaMapaCalorRequestDto filtros,
			String actaCodigoEstado) {

		String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

		List<VwPrSenadoresDistritoElectoralMultiple> registros = null;
		if(tieneEleccionAndFiltro.and(tieneDistritoElectoral.negate()).test(filtros)){
		registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
		return construirRespuestaSenadores27(registros,filtros.getCodigoAgrupacionPolitica(),actaCodigoEstado);
		} else if(tieneEleccionAndFiltro.and(tieneDistritoElectoral).test(filtros)){
		registros = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtros.getIdEleccion(), tipoFiltro,filtros.getIdDistritoElectoral());
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
					String apellido = registroDetalle.getCandidato().get(0).getApellidoPaterno().concat(" ").concat(registroDetalle.getCandidato().get(0).getApellidoMaterno());
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

		if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.SENADORES_33.getCodigo().toString()))) {
			filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.SENADORES_33.getCodigo().toString()));
			resultados.addAll(procesarResultados(
					lstEleccion,
					this.obtenerResumenSenadores33(tieneEleccionAndFiltro,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
			));
		}

		if (lstIdEleccion.contains(Integer.parseInt(TipoEleccionEnum.SENADORES_27.getCodigo().toString()))) {
			filtros.setIdEleccion(Integer.parseInt(TipoEleccionEnum.SENADORES_27.getCodigo().toString()));
			resultados.addAll(procesarResultados(
					lstEleccion,
					this.obtenerResumenSenadores27(tieneEleccionAndFiltro,tieneDistritoElectoral,tieneAmbito,tieneUbigeo1,tieneUbigeo2,tieneUbigeo3,filtros)
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

				Aggregation aggregationModulo = Aggregation.newAggregation(
						Aggregation.match(Criteria.where("n_eleccion").is(eleccion.get().getId())),
						Aggregation.match(Criteria.where("n_activo").is(1)),
						Aggregation.project("_id", "c_nombre")
				);

				MaeModulo maeModulo = primaryMongo.aggregate(
						aggregationModulo,
						"mae_modulo",
						MaeModulo.class
				).getUniqueMappedResult();

				String nombreEleccion = Optional.ofNullable(maeModulo)
						.map(MaeModulo::getNombre)
						.orElse(eleccion.get().getNombre());

	            VistaResumenGeneralDto dto = new VistaResumenGeneralDto();
	            dto.setId(Long.parseLong(eleccion.get().getCodigo()));
	            dto.setNombre(nombreEleccion);
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


	@Override
	public Page<VistaResumenGeneralDto> obtenerRevocatorias(FiltroRevocatoriasDto filtro, int pagina, int tamanio) {
		Optional<MaeEleccion> eleccion = this.maeEleccionRepository.findById(Long.parseLong(filtro.getIdEleccion().toString()));

	    if (eleccion.isEmpty()) {
	        return Page.empty();
	    }

		FiltroActaEleccionDto filtros = FiltroActaEleccionDto.builder()
				.idEleccion(filtro.getIdEleccion())
				.tipoFiltro(filtro.getTipoFiltro())
				.idAmbitoGeografico(filtro.getIdAmbitoGeografico())
				.build();
		
		Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroActaEleccionDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
		
		if (filtro.getIdEleccion().equals(Integer.parseInt(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo().toString()))) {
			Pageable pageable = PageRequest.of(pagina, tamanio);
			Page<VwPrRevocatoriaDistrital> paginaResultados = this.obtenerRevocatoriaDistritalPageable(
	                tieneEleccionAndFiltro, 
	                tieneAmbito, 
	                tieneUbigeo1, 
	                tieneUbigeo2, 
	                tieneUbigeo3, 
	                filtros, 
	                pageable);
	        
	        return paginaResultados.map(vwPrEleccion -> procesarResultadoRevocatoria(eleccion, vwPrEleccion));
	    }
		return Page.empty();
	}

	private <T extends VwPrEleccionBase> VistaResumenGeneralDto procesarResultadoRevocatoria(
			Optional<MaeEleccion> eleccion,
			T elecciones) {

		List<DetUbigeoEleccion> detUbigeoEleccion = detUbigeoEleccionRepository.findByEleccion(new MaeEleccion(eleccion.get().getId()));
		List<Long> ids = detUbigeoEleccion.stream().map(DetUbigeoEleccion::getIdUbigeo).toList();
		List<MaeUbigeo> lstUbigeo = this.maeUbigeoRepository.findByIds(ids);

		String ubigeoDesc = lstUbigeo.stream()
				.filter(f -> f.getId().equals(Long.parseLong(elecciones.getUbigeoNivel03().toString())))
				.map(MaeUbigeo::getCNombre)
				.findFirst()
				.orElse("");

		return VistaResumenGeneralDto.builder()
				.id(Long.parseLong(eleccion.get().getCodigo()))
				.nombre(eleccion.get().getNombre())
				.ubigeoNivel01(elecciones.getUbigeoNivel01())
				.ubigeoNivel02(elecciones.getUbigeoNivel02())
				.ubigeoNivel03(elecciones.getUbigeoNivel03())
				.actasContabilizadas(elecciones.getActasContabilizadas())
				.porcentajeActasContabilizadas(elecciones.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(elecciones.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(elecciones.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(elecciones.getActasPendientes())
				.porcentajeActasPendientes(elecciones.getPorcentajeActasPendientes())
				.ubigeoDesc(ubigeoDesc)
				.build();
	}
	
	@Override
	public List<VistaResumenGeneralDto> obtenerRevocatoriasv1(FiltroRevocatoriasDto filtro) {
		Optional<MaeEleccion> eleccion = this.maeEleccionRepository.findById(Long.parseLong(filtro.getIdEleccion().toString()));
		List<VistaResumenGeneralDto> resultados = new ArrayList<>();

	    if (eleccion.isEmpty()) {
	        return new ArrayList<>();
	    }

		FiltroActaEleccionDto filtros = FiltroActaEleccionDto.builder()
				.idEleccion(filtro.getIdEleccion())
				.tipoFiltro(filtro.getTipoFiltro())
				.idAmbitoGeografico(filtro.getIdAmbitoGeografico())
				.build();
		
		Predicate<FiltroActaEleccionDto> tieneEleccionAndFiltro = data ->data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroActaEleccionDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo1 = data -> data.getIdUbigeoDepartamento()!= null && data.getIdUbigeoDepartamento() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo2 = data -> data.getIdUbigeoProvincia()!= null && data.getIdUbigeoProvincia() != 0;
		Predicate<FiltroActaEleccionDto> tieneUbigeo3 = data -> data.getIdUbigeoDistrito()!= null && data.getIdUbigeoDistrito() != 0;
		
		if (filtro.getIdEleccion().equals(Integer.parseInt(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo().toString()))) {
			List<VwPrRevocatoriaDistrital> lstRevocatoriaDistrital = this.obtenerRevocatoriaDistrital(tieneEleccionAndFiltro, tieneAmbito, tieneUbigeo1, tieneUbigeo2, tieneUbigeo3, filtros);
			
			List<DetUbigeoEleccion> detUbigeoEleccion = detUbigeoEleccionRepository.findByEleccion(new MaeEleccion(eleccion.get().getId()));
			List<Long> ids = detUbigeoEleccion.stream().map(DetUbigeoEleccion::getIdUbigeo).toList();
			List<MaeUbigeo> lstUbigeo = this.maeUbigeoRepository.findByIds(ids);
			
			resultados = lstRevocatoriaDistrital.stream().map(vwPrEleccion -> procesarResultadoRevocatoriav1(eleccion, vwPrEleccion, lstUbigeo))
	        		.sorted(Comparator.comparing(VistaResumenGeneralDto::getUbigeoDesc))
	        		.toList();
	    }
		return resultados;
	}
	
	private <T extends VwPrEleccionBase> VistaResumenGeneralDto procesarResultadoRevocatoriav1(Optional<MaeEleccion> eleccion, T elecciones, List<MaeUbigeo> lstUbigeo) {		        	
		String ubigeoDesc = lstUbigeo.stream()
				.filter(f -> f.getId().equals(Long.parseLong(elecciones.getUbigeoNivel03().toString())))
		        .map(MaeUbigeo::getCNombre)
		        .findFirst()
		        .orElse("");
		
		return VistaResumenGeneralDto.builder()
				.id(Long.parseLong(eleccion.get().getCodigo()))
		        .nombre(eleccion.get().getNombre())
		        .ubigeoNivel01(elecciones.getUbigeoNivel01())
		        .ubigeoNivel02(elecciones.getUbigeoNivel02())
		        .ubigeoNivel03(elecciones.getUbigeoNivel03())
		        .actasContabilizadas(elecciones.getActasContabilizadas())
		        .porcentajeActasContabilizadas(elecciones.getPorcentajeActasContabilizadas())
		        .actasObservadasEnviadas(elecciones.getActasObservadasEnviadas())
		        .porcentajeActasObservadasEnviadas(elecciones.getPorcentajeActasObservadasEnviadas())
		        .actasPendientes(elecciones.getActasPendientes())
		        .porcentajeActasPendientes(elecciones.getPorcentajeActasPendientes())
		        .ubigeoDesc(ubigeoDesc)
		        .build();
	}

	private String obtenerTipoFiltro(String tipoFiltro)  {

		return switch (tipoFiltro) {
			case "total" -> "eleccion";
			case "eleccion" -> "ambito_geografico";
			case "ambito_geografico" -> "ubigeo_nivel_01";
			case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
			case "ubigeo_nivel_02", "ubigeo_nivel_03" -> "ubigeo_nivel_03";
            case DISTRITO_ELECTORAL -> DISTRITO_ELECTORAL;
			default -> "";
		};
	}
	
}
 