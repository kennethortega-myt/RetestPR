package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.TabArchivoReporteCandidatoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.secondary.TabArchivoReporteRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.TabArchivoReporteService;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TabArchivoReporteServiceImpl implements TabArchivoReporteService {

	private final TabArchivoReporteRepository archivoReporteRepository;
	private final TabArchivoReporteCandidatoRepository archivoReporteCandidatoRepository;

	@Override
	public Optional<TabArchivo> getArchivoById(String idArchivo) {
		return this.archivoReporteRepository.findById(idArchivo);
	}

	@Override
	public Optional<TabReporteCandidato> findByIdAndActivo(Integer codigoTipoEleccion, Integer activo) {
		return this.archivoReporteCandidatoRepository.findByIdAndActivo(codigoTipoEleccion, activo);
	}
}
