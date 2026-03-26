package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.TabArchivoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.TabArchivoService;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TabArchivoServiceImpl implements TabArchivoService {


	private final TabArchivoRepository archivoRepository;

	@Override
	public Optional<TabArchivo> getArchivoById(String idActa) {
		return this.archivoRepository.findById(idActa);
	}


}
