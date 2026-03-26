package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.TabArchivoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabArchivoService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TabArchivoServiceImpl implements TabArchivoService {


	private final TabArchivoRepository archivoRepository;

	@Override
	public Optional<TabArchivo> getArchivoById(String idActa) {
		return this.archivoRepository.findById(idActa);
	}
	
	

}
