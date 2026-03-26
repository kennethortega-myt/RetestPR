package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.presentacionbackend.model.dto.actas.*;

public interface ActaService extends CrudService<VwPrActa> {
	ActaResponseDto obtenerActaPorId(Long id);
	Optional<ActaPaginaResponseDto> obtenerActas(ActaRequestDto filtro, int pagina, int tamanio);
	Optional<ActaPaginaResponseDto> obtenerActasObservadas(ActaRequestDto filtro, int pagina, int tamanio);
	List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro);
	List<ActaLocalesResponseDto> obtenerActaLocales(ActaLocalesRequestDto filtro);
	List<ActaAgrupacionCandidatoRes> listarCandidatos(Long id, Long idAgru);
}
