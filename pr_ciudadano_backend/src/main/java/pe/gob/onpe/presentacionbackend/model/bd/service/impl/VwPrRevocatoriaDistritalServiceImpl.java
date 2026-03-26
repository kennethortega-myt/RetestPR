package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrRevocatoriaDistrital;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrRevocatoriaDistritalRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrRevocatoriaDistritalService;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.CandidatoResDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteDetalleCandidatoDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteDetalleDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteReqDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteResDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.TotalesDistritalesDto;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

@Service
@RequiredArgsConstructor
public class VwPrRevocatoriaDistritalServiceImpl implements VwPrRevocatoriaDistritalService {
	
	private static final Logger log = LoggerFactory.getLogger(VwPrRevocatoriaDistritalServiceImpl.class);
	
	private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;
	private final MaeUbigeoRepository maeUbigeoRepository;
	private final MaeCandidatoRepository maeCandidatoRepository;

	private static final String PA_ELECCION = "eleccion";

	@Override
	public void save(VwPrRevocatoriaDistrital k) {
		this.vwPrRevocatoriaDistritalRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrRevocatoriaDistrital> k) {
		this.vwPrRevocatoriaDistritalRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrRevocatoriaDistritalRepository.deleteAll();
		
	}

	@Override
	public List<VwPrRevocatoriaDistrital> findAll() {
		return this.vwPrRevocatoriaDistritalRepository.findAll();
	}

	@Override
	public Page<ParticipanteDto> listarParticipantes(String cargo) {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "c_detalle.c_candidato.n_total_votos"));
		Page<VwPrRevocatoriaDistrital> datos = this.vwPrRevocatoriaDistritalRepository.findByDetalleCargo(cargo, pageable);
		List<MaeUbigeo> ubigeos = maeUbigeoRepository.findAllById(datos.getContent().stream().filter(f->f.getUbigeoNivel03() != null).map(d->d.getUbigeoNivel03().longValue()).toList());
		
		List<ParticipanteDto> participantes = datos.getContent().stream()
				.filter(v -> !v.getTipoFiltro().equalsIgnoreCase(PA_ELECCION))
	            .map(entity -> mapToParticipanteDto(entity, cargo, ubigeos))
	            .toList();
	    return new PageImpl<>(participantes, pageable, datos.getTotalElements());

	}
	
	@Override
	public List<ParticipanteDto> listarParticipantesv1(String cargo) {
		// Obtener datos y ubigeos
	    List<VwPrRevocatoriaDistrital> datos = this.vwPrRevocatoriaDistritalRepository.findByDetalleCargoV1(cargo);
	    List<MaeUbigeo> ubigeos = maeUbigeoRepository.findAllById(
	        datos.stream()
	            .filter(f -> f.getUbigeoNivel03() != null)
	            .map(d -> d.getUbigeoNivel03().longValue())
	            .toList()
	    );
	    
	    List<MaeCandidato> lstCandidatos = this.maeCandidatoRepository.findByEleccion(new MaeEleccion(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo()));

	    // Procesar todos los detalles juntos
	    return datos.stream()
	        .filter(v -> v.getTipoFiltro() != null && !v.getTipoFiltro().equalsIgnoreCase(PA_ELECCION))
	        .flatMap(entity -> {
	            MaeUbigeo ubigeo = ubigeos.stream()
	                .filter(ubi -> ubi != null && ubi.getId() != null && 
	                       entity.getUbigeoNivel03() != null && 
	                       ubi.getId().equals(entity.getUbigeoNivel03().longValue()))
	                .findFirst()
	                .orElse(null);
	            
	            return entity.getDetalle().stream()
	                .filter(d -> d.getCargo() != null && d.getCargo().equalsIgnoreCase(cargo))
	                .map(detalle -> {
	                    List<ParticipanteDetalleCandidatoDto> candidatos = detalle.getCandidato().stream()
	                        .map(candidato -> ParticipanteDetalleCandidatoDto.builder()
	                            .posicionOpcionVoto(candidato.getPosicionOpcionVoto())
	                            .codigoOpcionVoto(candidato.getCodigoOpcionVoto())
	                            .totalVotos(candidato.getVotos())
	                            .descripcionOpcionVoto(candidato.getDescripcionOpcionVoto())
	                            .porcentajeVotosEmitidos(candidato.getPorcentajeVotosEmitidos())
	                            .porcentajeVotosValidos(candidato.getPorcentajeVotosValidos())
	                            .build())
	                        .sorted(Comparator.comparingInt(ParticipanteDetalleCandidatoDto::getPosicionOpcionVoto))
	                        .toList();
	                    Integer sexo = lstCandidatos.stream()
	                    		.filter(f->f.getDocumentoIdentidad().equals(detalle.getCodigo()))
	                    		.map(MaeCandidato::getSexo)
	                    		.findFirst()
	                    		.orElse(0);
	                    
	                    return ParticipanteDto.builder()
	                        .ubigeoNivel03(entity.getUbigeoNivel03())
	                        .ubigeoDesc(ubigeo != null ? ubigeo.getCNombre() : null)
	                        .nombreAgrupacionPolitica(detalle.getDescripcion())
	                        .codigoAgrupacionPolitica(detalle.getAgrupacionPolitica())
	                        .cargo(detalle.getCargo())
	                        .sexo(sexo)
	                        .candidato(candidatos)
	                        .build();
	                });
	        })
	        .sorted(Comparator.comparing(
	            ParticipanteDto::getNombreAgrupacionPolitica,
	            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
	        ))
	        .toList();
	}

	@Override
	public TotalesDistritalesDto obtenerTotales() {
		List<VwPrRevocatoriaDistrital> lista = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(7,PA_ELECCION);
		if(lista.isEmpty()){
			return null;
		} else {
			VwPrRevocatoriaDistrital registro = lista.get(0);
			int totalAlcaldes = registro.getDetalle().stream().filter(ele -> ele.getCargo().equals("Alcalde")).map(VwPrEleccionBaseDetalle::getCantidad).findFirst().orElse(0);
			int totalRegidores = registro.getDetalle().stream().filter(ele -> ele.getCargo().equals("Regidor")).map(VwPrEleccionBaseDetalle::getCantidad).findFirst().orElse(0);
			int total = registro.getDetalle().stream().filter(ele -> ele.getCargo().equals("Total")).map(VwPrEleccionBaseDetalle::getCantidad).findFirst().orElse(0);

			return
			TotalesDistritalesDto.builder()
					.totalAlcaldes(totalAlcaldes)
					.totalRegidores(totalRegidores)
					.total(total)
					.build();
		}
	}

	private ParticipanteDto mapToParticipanteDto(VwPrRevocatoriaDistrital entity, String cargo, List<MaeUbigeo> ubigeos) {
		List<ParticipanteDetalleDto> detalle1 = entity.getDetalle().stream()
				.filter(d -> d.getCargo().equals(cargo))
	            .map(detalle -> {
	                List<ParticipanteDetalleCandidatoDto> candidatos = detalle.getCandidato().stream()
	                        .map(candidato -> ParticipanteDetalleCandidatoDto.builder()
	                                .posicionOpcionVoto(candidato.getPosicionOpcionVoto())
	                                .codigoOpcionVoto(candidato.getCodigoOpcionVoto())
	                                .totalVotos(candidato.getVotos())
	                                .descripcionOpcionVoto(candidato.getDescripcionOpcionVoto())
	                                .porcentajeVotosEmitidos(candidato.getPorcentajeVotosEmitidos())
	                                .porcentajeVotosValidos(candidato.getPorcentajeVotosValidos())
	                                .build()
	                        ).toList();
	                
	                return ParticipanteDetalleDto.builder()
	                        .nombreAgrupacionPolitica(detalle.getDescripcion())
	                        .codigoAgrupacionPolitica(detalle.getAgrupacionPolitica())
	                        .cargo(detalle.getCargo())
	                        .candidato(candidatos)
	                        .build();
	            })
	            .toList();
		
		MaeUbigeo ubigeo = Optional.ofNullable(ubigeos)
				.orElseGet(Collections::emptyList)
				.stream()
				.filter(ubi -> entity != null && ubi.getId() != null && ubi.getId().equals(entity.getUbigeoNivel03().longValue()))
				.findFirst()
				.orElse(null);
		
		return ParticipanteDto.builder()
				.ubigeoNivel03(entity.getUbigeoNivel03())
				.ubigeoDesc((ubigeo != null && ubigeo.getCNombre()!=null)?ubigeo.getCNombre():"")
				.detalle(detalle1)
				.build();
	}

	@Override
	public List<ParticipanteResDto> listarParticipantesUbicacionGeografica(ParticipanteReqDto filtros) {
		Predicate<ParticipanteReqDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<ParticipanteReqDto> tieneAmbito = data ->  data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<ParticipanteReqDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<ParticipanteReqDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<ParticipanteReqDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;


		List<VwPrRevocatoriaDistrital> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros, filtros.getCodigoAgrupacionPolitica());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros, filtros.getCodigoAgrupacionPolitica());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros, filtros.getCodigoAgrupacionPolitica());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros, filtros.getCodigoAgrupacionPolitica());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros, filtros.getCodigoAgrupacionPolitica());
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantesUbicacionGeografica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}
	
	private List<ParticipanteResDto> construirRespuesta(List<VwPrRevocatoriaDistrital> registros, Integer codigo){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		
		MaeUbigeo ubigeo = maeUbigeoRepository.findById(registros.get(0).getUbigeoNivel03().longValue()).orElse(null);
		
		List<VwPrEleccionBaseDetalle> detalleRevoDist = registros.get(0).getDetalle().stream()
				.filter(data -> data.getVotos() != null)
				.filter(data -> data.getAgrupacionPolitica().compareTo(codigo) == 0)
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.toList();

		return  detalleRevoDist.stream().map(detalle -> mapperCampos(detalle,ubigeo.getCNombre()))
				.toList();
	}
	
	private static ParticipanteResDto mapperCampos(VwPrEleccionBaseDetalle registro, String ubigeoDesc){
		List<CandidatoResDto> candidatos = registro.getCandidato().stream()
				.map(candidato -> {
					CandidatoResDto candidatoDto = new CandidatoResDto();
					candidatoDto.setVotos(candidato.getVotos());
					candidatoDto.setPosicionOpcionVoto(candidato.getPosicionOpcionVoto());
					candidatoDto.setCodigoOpcionVoto(candidato.getCodigoOpcionVoto());
					candidatoDto.setDescripcionOpcionVoto(candidato.getDescripcionOpcionVoto());
					candidatoDto.setCargo(candidato.getCargo());
					candidatoDto.setPorcentajeVotosValidos(candidato.getPorcentajeVotosValidos());
					candidatoDto.setPorcentajeVotosEmitidos(candidato.getPorcentajeVotosEmitidos());
					return candidatoDto;
				})
				.sorted(Comparator.comparingInt(CandidatoResDto::getPosicionOpcionVoto))
				.toList();
		return ParticipanteResDto.builder()
				.candidato(candidatos)
				.nombreCandidato(registro.getDescripcion())
				.totalVotosEmitidos(registro.getVotos())
				.totalVotosValidos(registro.getTotalVotosValidos())
				.ubigeoDesc(ubigeoDesc)
				.build();
	}
	
}
