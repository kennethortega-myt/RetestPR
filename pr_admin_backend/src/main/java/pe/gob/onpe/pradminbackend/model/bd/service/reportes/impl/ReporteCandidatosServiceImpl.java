package pe.gob.onpe.pradminbackend.model.bd.service.reportes.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetCatalogoEstructura; // Added import
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.pradminbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.pradminbackend.model.bd.repository.DetCatalogoEstructuraRepository; // Added import
import pe.gob.onpe.pradminbackend.model.bd.repository.DetUbigeoEleccionAgrupacionPoliticaRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeCandidatoRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeEleccionRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeImportarRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.TabReporteCandidatoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ReporteCandidatosService;
import pe.gob.onpe.pradminbackend.model.dto.reportes.GenerarCsvResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.s3.S3Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.text.Collator;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteCandidatosServiceImpl implements ReporteCandidatosService {

    private final MaeImportarRepository maeImportarRepository;
    private final MaeCandidatoRepository maeCandidatoRepository;
    private final MaeEleccionRepository maeEleccionRepository;
    private final TabReporteCandidatoRepository tabReporteCandidatoRepository;
    private final DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository;
    private final DetCatalogoEstructuraRepository detCatalogoEstructuraRepository;
    private final S3Service s3Service;

    @Value("${pr.nfs.path}")
    private String pathNfs;

    @Value("${pr.nfs.files}")
    private String pathFiles;

    @Value("${despliegue-nube}")
    private String despliegueNube;

    @Override
    public GenericResponse<GenerarCsvResponseDto> generarCsvCandidatos(Integer idTipoEleccion, String username) {
        log.info("Iniciando generación de CSV para idTipoEleccion: {} por usuario: {}", idTipoEleccion, username);

        if (tabReporteCandidatoRepository.count() == 0) {
            log.info("Inicializando la colección tab_reporte_candidato...");
            List<TabReporteCandidato> reportesIniciales = IntStream.rangeClosed(10, 15)
                    .filter(id -> id != 11)
                    .mapToObj(id -> TabReporteCandidato.builder()
                            .id(id)
                            .exito(false)
                            .ruta("")
                            .nActivo(1)
                            .cAudUsuarioCreacion(username)
                            .fechaCreacion(new Date())
                            .build())
                    .toList();
            tabReporteCandidatoRepository.saveAll(reportesIniciales);
            log.info("Colección tab_reporte_candidato inicializada.");
        }

        if (maeImportarRepository.existsByExito(false)) {
            log.warn("No se puede generar el CSV: Existen registros con exito=false en MaeImportar.");
            return new GenericResponse<>(false, "No hay data cargada", null);
        }

        Optional<MaeEleccion> eleccionOpt = maeEleccionRepository.findByCodigo(String.valueOf(idTipoEleccion));
        if (eleccionOpt.isEmpty()) {
            log.error("No se encontró la elección con el código: {}", idTipoEleccion);
            return new GenericResponse<>(false, "Tipo de elección no válido", null);
        }
        MaeEleccion eleccion = eleccionOpt.get();

        // Load cargo mappings
        Map<Integer, String> cargoMap = detCatalogoEstructuraRepository.findByColumna("n_cargo")
                .stream()
                .collect(Collectors.toMap(DetCatalogoEstructura::getCodigo, DetCatalogoEstructura::getNombre, (existing, replacement) -> existing));

        // Obtener y ordenar la lista de candidatos
        List<MaeCandidato> candidatos = maeCandidatoRepository.findByEleccion(eleccion);
        ordenarCandidatos(candidatos, eleccion, idTipoEleccion);

        try {
            String fileName;
            String[] headers;

            final String headerNombreEleccion = "nombreEleccion";
            final String headerOrganizacionPolitica = "organizacionPolitica";
            final String headerNombre = "nombre";
            final String headerApPaterno = "apPaterno";
            final String headerApMaterno = "apMaterno";
            final String headerNumeroCandidato = "numeroCandidato";
            final String headerDistritoElectoral = "distritoElectoral";
            final String headerCargo = "cargo"; // New header

            switch (idTipoEleccion) {
                case 10:
                    fileName = "CandidatosPresidenciales.csv";
                    headers = new String[]{headerNombreEleccion, headerOrganizacionPolitica, headerNombre, headerApPaterno, headerApMaterno, headerCargo};
                    break;
                case 14:
                    fileName = "CandidatosSenadoresDEM.csv";
                    headers = new String[]{headerNombreEleccion, headerOrganizacionPolitica, headerDistritoElectoral, headerNumeroCandidato, headerNombre, headerApPaterno, headerApMaterno, headerCargo};
                    break;
                case 15:
                    fileName = "CandidatosSenadoresDEU.csv";
                    headers = new String[]{headerNombreEleccion, headerOrganizacionPolitica, headerNumeroCandidato, headerNombre, headerApPaterno, headerApMaterno, headerCargo};
                    break;
                case 13:
                    fileName = "CandidatosDiputados.csv";
                    headers = new String[]{headerNombreEleccion, headerOrganizacionPolitica, headerDistritoElectoral, headerNumeroCandidato, headerNombre, headerApPaterno, headerApMaterno, headerCargo};
                    break;
                case 12:
                    fileName = "CandidatosParlamentoAndino.csv";
                    headers = new String[]{headerNombreEleccion, headerOrganizacionPolitica, headerNumeroCandidato, headerNombre, headerApPaterno, headerApMaterno, headerCargo};
                    break;
                default:
                    log.error("idTipoEleccion no válido: {}", idTipoEleccion);
                    return new GenericResponse<>(false, "idTipoEleccion no válido", null);
            }

            String url = pathNfs.concat(pathFiles).concat("/Maestro_de_candidatos/");
            String filePath = url + fileName;
            Files.createDirectories(Paths.get(url));

            try (
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            ) {
                for (MaeCandidato candidato : candidatos) {
                    String cargoNombre = cargoMap.getOrDefault(candidato.getCargo(), ""); // Get cargo name

                    try {
                        switch (idTipoEleccion) {
                            case 10:
                                csvPrinter.printRecord(
                                        candidato.getEleccion().getNombre(),
                                        candidato.getAgrupacionPolitica().getDescripcion(),
                                        candidato.getNombres(),
                                        candidato.getApellidoPaterno(),
                                        candidato.getApellidoMaterno(),
                                        cargoNombre // Added cargo
                                );
                                break;
                            case 14, 13:
                                csvPrinter.printRecord(
                                        candidato.getEleccion().getNombre(),
                                        candidato.getAgrupacionPolitica().getDescripcion(),
                                        candidato.getDistritoElectoral().getNombre(),
                                        candidato.getLista(),
                                        candidato.getNombres(),
                                        candidato.getApellidoPaterno(),
                                        candidato.getApellidoMaterno(),
                                        cargoNombre // Added cargo
                                );
                                break;
                            case 15, 12:
                                csvPrinter.printRecord(
                                        candidato.getEleccion().getNombre(),
                                        candidato.getAgrupacionPolitica().getDescripcion(),
                                        candidato.getLista(),
                                        candidato.getNombres(),
                                        candidato.getApellidoPaterno(),
                                        candidato.getApellidoMaterno(),
                                        cargoNombre // Added cargo
                                );
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        log.error("Error al escribir en el archivo CSV", e);
                    }
                }
            }

            log.info("Archivo CSV generado exitosamente en: {}", filePath);

            if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                s3Service.copiarReporteDesdeEfsAS3(fileName, url);
            }

            Optional<TabReporteCandidato> reporteCandidatoOpt = tabReporteCandidatoRepository.findById(idTipoEleccion);
            if (reporteCandidatoOpt.isPresent()) {
                TabReporteCandidato reporteCandidato = reporteCandidatoOpt.get();
                reporteCandidato.setExito(true);
                reporteCandidato.setRuta(filePath);
                reporteCandidato.setCAudUsuarioModificacion(username);
                reporteCandidato.setDAudFechaModificacion(new Date());
                tabReporteCandidatoRepository.save(reporteCandidato);
                log.info("Registro de reporte para id {} actualizado.", idTipoEleccion);
            } else {
                log.warn("No se encontró un registro de reporte para el id {}. No se pudo actualizar el estado.", idTipoEleccion);
            }

            return new GenericResponse<>(true, "Archivo CSV generado exitosamente", new GenerarCsvResponseDto(filePath));

        } catch (IOException e) {
            log.error("Error al generar el archivo CSV {}", e.getMessage());
            return new GenericResponse<>(false, "Error al generar el archivo CSV", null);
        }
    }

    private void ordenarCandidatos(List<MaeCandidato> candidatos, MaeEleccion eleccion, Integer idTipoEleccion) {
        Map<Long, Integer> posicionPorAgrupacion = detUbigeoEleccionAgrupacionPoliticaRepository.findByUbigeoEleccionEleccion(eleccion)
                .stream()
                .collect(Collectors.toMap(
                        det -> det.getAgrupacionPolitica().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing // En caso de duplicados, mantener el existente
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPosicion()));

        // Create a Collator instance for Spanish, ignoring accents and case
        Collator esCollator = Collator.getInstance(new Locale("es", "PE")); // Or Locale.getDefault()
        esCollator.setStrength(Collator.PRIMARY); // Ignore accents and case

        Comparator<MaeCandidato> comparador;

        switch (idTipoEleccion) {
            case 10: // Presidente
                // Sort by political organization name, ignoring accents and case, then by cargo
                comparador = Comparator.comparing((MaeCandidato c) -> c.getAgrupacionPolitica().getDescripcion(), esCollator)
                        .thenComparing(MaeCandidato::getCargo);
                break;

            case 15, 12: // Senadores DEU // Parlamento Andino
                // Sort by political organization name, then by candidate number, then by cargo
                comparador = Comparator.comparing((MaeCandidato c) -> c.getAgrupacionPolitica().getDescripcion(), esCollator)
                        .thenComparing(MaeCandidato::getLista)
                        .thenComparing(MaeCandidato::getCargo);
                break;

            case 14, 13: // Senadores DEM // Diputados
                // Sort by political organization name, then by electoral district ID, then by candidate number, then by cargo
                comparador = Comparator.comparing((MaeCandidato c) -> c.getAgrupacionPolitica().getDescripcion(), esCollator)
                        .thenComparing(c -> c.getDistritoElectoral().getId())
                        .thenComparing(MaeCandidato::getLista)
                        .thenComparing(MaeCandidato::getCargo);
                break;

            default:
                return; // No se aplica ordenamiento
        }

        candidatos.sort(comparador);
    }
}
