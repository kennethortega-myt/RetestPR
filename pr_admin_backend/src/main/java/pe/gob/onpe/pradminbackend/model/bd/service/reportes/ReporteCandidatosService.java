package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import pe.gob.onpe.pradminbackend.model.dto.reportes.GenerarCsvResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

public interface ReporteCandidatosService {
    GenericResponse<GenerarCsvResponseDto> generarCsvCandidatos(Integer idTipoEleccion, String username);
}
