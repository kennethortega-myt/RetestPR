package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabCronReporteActas;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.ActaRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.TabArchivoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteActasRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.ProcesamientoActasService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ValidarPorcentajeService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.TramaScePuestaCeroDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ArchivoProcesamientoDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.s3.S3Service;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;
import pe.gob.onpe.consultaopbackend.utils.DateTimeUtil;
import pe.gob.onpe.consultaopbackend.utils.DateUtil;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ProcesamientoActasServiceImpl implements ProcesamientoActasService {

    private final TabReporteActasRepository tabProgramacionReporteRepository;
    private final TabArchivoRepository tabArchivoRepository;
    private final ActaRepository vwPrActaRepository;
    private final ValidarPorcentajeService validarPorcentajeService;

    private final MongoTemplate primaryMongoTemplate;
    private final MongoTemplate secondaryMongoTemplate;
    private final ValidacionCronServiceImpl validacionCronService;
    private final S3Service s3Service;

    @Value("${despliegue-nube}")
    private String despliegueNube;

    @Value("${cop.nfs.path}")
    private String COP_NFS_PATH;

    @Value("${cop.nfs.files}")
    private String COP_PATH_FILES;

    @Value("${pr.nfs.actas.origen}")
    private String RUTA_ORIGEN;

    @Value("${pr.nfs.actas.destino}")
    private String RUTA_BASE_DESTINO;

    public ProcesamientoActasServiceImpl(
            TabReporteActasRepository tabProgramacionReporteRepository,
            TabArchivoRepository tabArchivoRepository,
            ActaRepository vwPrActaRepository,
            ValidarPorcentajeService validarPorcentajeService,
            ValidacionCronServiceImpl validacionCronService,
            S3Service s3Service,
            @Qualifier("primaryMongoTemplate") MongoTemplate primaryMongoTemplate,
            @Qualifier("secondaryMongoTemplate") MongoTemplate secondaryMongoTemplate) {
        this.s3Service = s3Service;
        this.validacionCronService = validacionCronService;
        this.tabProgramacionReporteRepository = tabProgramacionReporteRepository;
        this.validarPorcentajeService = validarPorcentajeService;
        this.tabArchivoRepository = tabArchivoRepository;
        this.vwPrActaRepository = vwPrActaRepository;
        this.primaryMongoTemplate = primaryMongoTemplate;
        this.secondaryMongoTemplate = secondaryMongoTemplate;
    }

    @Override
    public ResponseEntity<ReporteCronResponse> procesarActas(String idConfiguracion) {
        GenericResponse<String> response = new GenericResponse<>();

        // 1. Obtener configuración del cron
        Optional<TabCronReporteActas> optConfig = tabProgramacionReporteRepository.findById(idConfiguracion);

        if (optConfig.isEmpty()) {
            log.error("Configuración no encontrada: {}", idConfiguracion);
            response.setSuccess(false);
            response.setMessage("Configuración no encontrada");
            return new ResponseEntity<>(
                    ReporteCronResponse.builder().response(response).build(),
                    HttpStatus.NOT_FOUND);
        }

        TabCronReporteActas config = optConfig.get();
        Double porcentajeProcesoActual= validarPorcentajeService
                .obtenerPorcentageContabilizado(config.getEleccionId());
        Double porcentajeCron = ObjectUtils.getIfNull(config.getPorcentaje(),0.0);

        if (porcentajeCron >= porcentajeProcesoActual ) {
            log.error("No procesar actas, porcentaje no cambió, porcentaje cron: {} ," +
                    " porcentaje proceso actual: {}",porcentajeCron, porcentajeProcesoActual);
            response.setSuccess(false);
            response.setMessage("Porcentaje no cambió");
            return new ResponseEntity<>(
                    ReporteCronResponse.builder().response(response).build(),
                    HttpStatus.NOT_FOUND);
        } else {
            try {

                log.info("=== INICIANDO PROCESAMIENTO DE ACTAS ===");
                log.info("ID Configuración: {}", idConfiguracion);

                // Pone en curso el estado de ejecucion del cron
                validacionCronService.actualizarEstadoReporteActasCronEjecucion(config,
                        TipoEstadoProcesoEnum.EN_CURSO);

                Long tipoEleccion = config.getEleccionId().longValue();
                String nombreEleccion = config.getEleccion();

                log.info("Tipo de elección: {} ({})", tipoEleccion, nombreEleccion);

                if (config.getEstado() == 0) {
                    log.warn("Configuración deshabilitada");
                    validacionCronService.actualizarEstadoReporteActasCronEjecucion(config,
                            TipoEstadoProcesoEnum.PENDIENTE);
                    response.setSuccess(false);
                    response.setMessage("Configuración deshabilitada");
                    return new ResponseEntity<>(
                            ReporteCronResponse.builder().response(response).build(),
                            HttpStatus.OK);
                }

                Path directorioBase;
                boolean esNube = Boolean.TRUE.equals(Boolean.valueOf(despliegueNube));

                if (esNube) {
                    directorioBase = Files.createTempDirectory("procesamiento_actas_");
                    log.info("Directorio temporal creado: {}", directorioBase);
                } else {
                    directorioBase = Paths.get(COP_NFS_PATH, COP_PATH_FILES, RUTA_BASE_DESTINO);
                    Files.createDirectories(directorioBase);
                }

                // 2. Ejecutar procesamiento
                Map<String, String> zipsGenerados = ejecutarProcesamiento(tipoEleccion, directorioBase);

                response.setSuccess(true);
                response.setMessage(String.format(
                        "Procesamiento completado. ZIPs generados: %d",
                        zipsGenerados.size()));


                // 3. Actualizar porcentaje acta cron

                config.setPorcentaje(porcentajeProcesoActual);
                tabProgramacionReporteRepository.save(config);

                log.info("=== PROCESAMIENTO COMPLETADO EXITOSAMENTE ===");
                validacionCronService.actualizarEstadoReporteActasCronEjecucion(config,
                        TipoEstadoProcesoEnum.PENDIENTE);

                if (esNube) {
                    FileUtils.deleteDirectory(directorioBase.toFile());
                    log.info("Directorio temporal eliminado");
                }

                return new ResponseEntity<>(
                        ReporteCronResponse.builder().response(response).build(),
                        HttpStatus.OK);

            } catch (Exception e) {
                log.error("Error en procesamiento de actas", e);
                validacionCronService.actualizarEstadoReporteActasCronEjecucion(config,
                        TipoEstadoProcesoEnum.ERROR);
                response.setSuccess(false);
                response.setMessage("Error: " + e.getMessage());
                return new ResponseEntity<>(
                        ReporteCronResponse.builder().response(response).build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    private Map<String, String> ejecutarProcesamiento(Long tipoEleccion, Path directorioBase) throws IOException {

        // PASO 1: Leer archivos de origen
        log.info("PASO 1: Leyendo archivos de {}", RUTA_ORIGEN);
        List<File> archivosOrigen = leerArchivosOrigen(directorioBase);
        //List<File> archivosOrigen = leerArchivosOrigen();
        log.info("Archivos encontrados en origen: {}", archivosOrigen.size());

        // PASO 2: Filtrar y enriquecer archivos
        log.info("PASO 2: Filtrando archivos por tipo de elección {}", tipoEleccion);
        List<ArchivoProcesamientoDto> archivosFiltrados = filtrarYEnriquecerArchivos(archivosOrigen, tipoEleccion);
        log.info("Archivos filtrados para procesamiento: {}", archivosFiltrados.size());

        if (archivosFiltrados.isEmpty()) {
            log.warn("No hay archivos para procesar");
            return Collections.emptyMap();
        }

        // PASO 3: Calcular nroOrden para resoluciones
        log.info("PASO 3: Calculando nroOrden para resoluciones");
        calcularNroOrdenResoluciones(archivosFiltrados);

        // PASO 4: Generar nuevos nombres
        log.info("PASO 4: Generando nuevos nombres de archivos");
        generarNuevosNombres(archivosFiltrados, tipoEleccion);

        // PASO 5: Copiar y renombrar archivos
        log.info("PASO 5: Copiando y renombrando archivos");
        copiarYRenombrarArchivos(archivosFiltrados, tipoEleccion, directorioBase);

        // PASO 6: Agrupar por región
        log.info("PASO 6: Agrupando archivos por región");
        Map<String, List<ArchivoProcesamientoDto>> archivosPorRegion = agruparPorRegion(archivosFiltrados);
        log.info("Regiones encontradas: {}", archivosPorRegion.keySet());

        // PASO 7: Mover archivos a carpetas de región
        log.info("PASO 7: Moviendo archivos a carpetas de región");
        moverArchivosARegiones(archivosPorRegion, tipoEleccion, directorioBase);

        // PASO 8: Generar ZIPs por región
        log.info("PASO 8: Generando ZIPs por región");
        Map<String, String> zipsGenerados = generarZipsPorRegion(archivosPorRegion, tipoEleccion, directorioBase);

        log.info("ZIPs generados: {}", zipsGenerados.size());
        zipsGenerados.forEach((region, ruta) -> log.info("  - {}: {}", region, ruta));

        return zipsGenerados;
    }

    private List<File> leerArchivosOrigen(Path directorioBase) {
        if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
            return descargarArchivosDesdeS3(directorioBase);
        } else {

            File directorio = new File(RUTA_ORIGEN);

            if (!directorio.exists() || !directorio.isDirectory()) {
                log.error("Directorio no existe o no es válido: {}", RUTA_ORIGEN);
                return Collections.emptyList();
            }

            File[] archivos = directorio.listFiles(File::isFile);

            if (archivos == null || archivos.length == 0) {
                log.warn("No hay archivos en el directorio origen");
                return Collections.emptyList();
            }

            return Arrays.asList(archivos);
        }
    }

    private List<File> descargarArchivosDesdeS3(Path directorioBase) {
        List<File> archivos = new ArrayList<>();
        try {
            List<String> claves = s3Service.listarObjetosBucketActas();
            for (String clave : claves) {
                String nombreArchivo = clave.substring(clave.lastIndexOf('/') + 1);
                if (nombreArchivo.isEmpty()) continue;
                Path destino = directorioBase.resolve(nombreArchivo);
                s3Service.descargarObjetoBucketActas(clave, destino.toString());
                archivos.add(destino.toFile());
                log.debug("Archivo descargado: {}", clave);
            }
            log.info("Total archivos descargados de S3: {}", archivos.size());
        } catch (Exception e) {
            log.error("Error descargando archivos de S3", e);
        }
        return archivos;
    }

    public List<TabArchivo> getArchivoBycGuid(String cGuid) {

        List<TabArchivo> resultados = this.tabArchivoRepository.findBycGuid(cGuid);

        if (resultados == null || resultados.isEmpty()) {
            log.warn("No se encontraron archivos con cGuid: {}", cGuid);
            return Collections.emptyList();
        }
        log.info("Resultados encontrados: " + resultados.size());
        return resultados;

    }

    private List<ArchivoProcesamientoDto> filtrarYEnriquecerArchivos(
            List<File> archivos, Long tipoEleccion) {

        List<ArchivoProcesamientoDto> resultado = new ArrayList<>();

        for (File archivo : archivos) {
            try {
                // Extraer GUID del nombre del archivo (sin extensión)
                String nombreArchivo = archivo.getName();
                String cGuid = extraerGuid(nombreArchivo);
                String extension = extraerExtension(nombreArchivo);

                // Buscar en tab_archivo
                List<TabArchivo> optTabArchivo = getArchivoBycGuid(cGuid); // tabArchivoRepository.findBycGuid(cGuid)

                optTabArchivo.forEach(tabArchivo -> {
                    Long nIdActa = tabArchivo.getIdActa();
                    Integer nTipo = tabArchivo.getTipo();

                    // Buscar en vw_pr_acta
                    Optional<VwPrActa> optVwPrActa = vwPrActaRepository.findById(nIdActa);
                    VwPrActa vwPrActa = optVwPrActa.get();

                    // Filtrar por tipo de elección
                    if (vwPrActa.getIdEleccion().equals(tipoEleccion)) {
                        // Crear DTO
                        ArchivoProcesamientoDto dto = ArchivoProcesamientoDto.builder()
                                .archivoOriginal(archivo)
                                .cGuid(cGuid)
                                .extension(extension)
                                .nIdActa(nIdActa)
                                .nTipo(nTipo)
                                .nEleccion(vwPrActa.getIdEleccion())
                                .cCodigoMesa(vwPrActa.getCodigoMesa())
                                .cUbigeoNivel01(vwPrActa.getUbigeoNombreNivel01())
                                .build();

                        resultado.add(dto);
                    }
                });

            } catch (Exception e) {
                log.error("Error procesando archivo: {}", archivo.getName(), e);
            }
        }

        return resultado;
    }

    private String extraerGuid(String nombreArchivo) {
        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot > 0) {
            return nombreArchivo.substring(0, lastDot);
        }
        return nombreArchivo;
    }

    private String extraerExtension(String nombreArchivo) {
        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot > 0) {
            return nombreArchivo.substring(lastDot); // Incluye el punto
        }
        return "";
    }

    private void calcularNroOrdenResoluciones(List<ArchivoProcesamientoDto> archivos) {
        // Agrupar por nIdActa solo las resoluciones
        Map<Long, List<ArchivoProcesamientoDto>> resolucionesPorActa = archivos.stream()
                .filter(a -> a.getNTipo() == 5) // Solo resoluciones
                .collect(Collectors.groupingBy(ArchivoProcesamientoDto::getNIdActa));

        // Para cada grupo, ordenar por fecha y asignar nroOrden
        resolucionesPorActa.forEach((nIdActa, resoluciones) -> {
            if (resoluciones.size() > 1) {
                // Ordenar por fecha de creación
                resoluciones.sort((a, b) -> {
                    try {
                        TabArchivo ta1 = getArchivoBycGuid(a.getCGuid()).getFirst();
                        TabArchivo ta2 = getArchivoBycGuid(b.getCGuid()).getFirst();

                        if (ta1 != null && ta2 != null) {
                            return ta1.getDAudFechaCreacion().compareTo(ta2.getDAudFechaCreacion());
                        }
                    } catch (Exception e) {
                        log.error("Error ordenando resoluciones", e);
                    }
                    return 0;
                });

                // Asignar nroOrden secuencial
                for (int i = 0; i < resoluciones.size(); i++) {
                    resoluciones.get(i).setNroOrden(i + 1);
                }

                log.debug("Acta {} tiene {} resoluciones", nIdActa, resoluciones.size());
            } else {
                resoluciones.getFirst().setNroOrden(1);
            }
        });

        // Para no resoluciones, nroOrden = 1
        archivos.stream()
                .filter(a -> a.getNTipo() != 5)
                .forEach(a -> a.setNroOrden(1));
    }

    private void generarNuevosNombres(
            List<ArchivoProcesamientoDto> archivos,
            Long tipoEleccion) {
        String tipoEleccionStr = obtenerNombreTipoEleccion(tipoEleccion);

        for (ArchivoProcesamientoDto archivo : archivos) {
            String tipoDocumento = obtenerNombreTipoDocumento(archivo.getNTipo());
            String nuevoNombre = String.format("%s-%s-%s%s",
                    archivo.getCCodigoMesa(),
                    tipoDocumento,
                    tipoEleccionStr,
                    archivo.getExtension());

            if (archivo.getNTipo() == 5) {
                nuevoNombre = String.format("%s-%s%d-%s%s",
                        archivo.getCCodigoMesa(),
                        tipoDocumento,
                        archivo.getNroOrden(),
                        tipoEleccionStr,
                        archivo.getExtension());
            }

            archivo.setNuevoNombre(nuevoNombre);
            log.debug("Renombrado: {} -> {}", archivo.getArchivoOriginal().getName(), nuevoNombre);
        }
    }

    private String obtenerNombreTipoDocumento(Integer nTipo) {
        return switch (nTipo) {
            case 1 -> "ActaEscrutinio";
            case 2 -> "ActaInstalacionSufragio";
            case 3 -> "ActaInstalacion";
            case 4 -> "ActaSufragio";
            case 5 -> "Resolucion";
            default -> ConstantesComunes.DESCONOCIDO;
        };
    }

    private String obtenerNombreTipoEleccion(Long nEleccion) {

        if (nEleccion == null) {
            return ConstantesComunes.DESCONOCIDO;
        } else {
            return switch (nEleccion.intValue()) {
                case 10 -> "Presidencial";
                case 12 -> "ParlamentoAndino";
                case 13 -> "Diputados";
                case 14 -> "SenadoresDEM";
                case 15 -> "SenadoresDEU";
                default -> ConstantesComunes.DESCONOCIDO;
            };
        }

    }

    private String obtenerNombreCarpetaTipoEleccion(Long nEleccion) {
        if (nEleccion == null) {
            return ConstantesComunes.DESCONOCIDO;
        } else {
            return switch (nEleccion.intValue()) {
                case 10 -> "Presidencial";
                case 12 -> "Parlamento_andino";
                case 13 -> "Diputados";
                case 14 -> "Senadores_dem";
                case 15 -> "Senadores_deu";
                default -> ConstantesComunes.DESCONOCIDO;
            };
        }
    }

    private void copiarYRenombrarArchivos(List<ArchivoProcesamientoDto> archivos, Long tipoEleccion, Path directorioBase) throws IOException {

        String carpetaTipoEleccion = obtenerNombreCarpetaTipoEleccion(tipoEleccion);
        Path rutaDestino = directorioBase.resolve(carpetaTipoEleccion);

        // Crear carpeta si no existe
        Files.createDirectories(rutaDestino);

        int copiados = 0;
        int errores = 0;

        for (ArchivoProcesamientoDto archivo : archivos) {
            try {
                Path origen = archivo.getArchivoOriginal().toPath();
                Path destino = rutaDestino.resolve(archivo.getNuevoNombre());

                Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
                copiados++;

            } catch (IOException e) {
                log.error("Error copiando archivo: {}", archivo.getArchivoOriginal().getName(), e);
                errores++;
            }
        }

        log.info("Archivos copiados: {} | Errores: {}", copiados, errores);
    }

    private Map<String, List<ArchivoProcesamientoDto>> agruparPorRegion(
            List<ArchivoProcesamientoDto> archivos) {

        return archivos.stream()
                .collect(Collectors.groupingBy(ArchivoProcesamientoDto::getCUbigeoNivel01));
    }

    private void moverArchivosARegiones(
            Map<String, List<ArchivoProcesamientoDto>> archivosPorRegion,
            Long tipoEleccion,
            Path directorioBase) throws IOException {

        String carpetaTipoEleccion = obtenerNombreCarpetaTipoEleccion(tipoEleccion);
        Path rutaBase = directorioBase.resolve(carpetaTipoEleccion);

        for (Map.Entry<String, List<ArchivoProcesamientoDto>> entry : archivosPorRegion.entrySet()) {
            String region = entry.getKey();
            List<ArchivoProcesamientoDto> archivos = entry.getValue();

            // Crear carpeta de región
            Path rutaRegion = rutaBase.resolve(region);
            Files.createDirectories(rutaRegion);

            Path rutaActasyResoluciones = rutaRegion.resolve("ActasyResoluciones");
            Files.createDirectories(rutaActasyResoluciones);

            log.info("Carpeta ActasyResoluciones creada: {}", rutaActasyResoluciones);

            // Mover archivos
            for (ArchivoProcesamientoDto archivo : archivos) {
                try {
                    Path origen = rutaBase.resolve(archivo.getNuevoNombre());
                    Path destino = rutaActasyResoluciones.resolve(archivo.getNuevoNombre());
                    Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    log.error("Error moviendo archivo a región: {}", archivo.getNuevoNombre(), e);
                }
            }

            log.info("Región {}: {} archivos", region, archivos.size());
        }
    }

    private Map<String, String> generarZipsPorRegion(
            Map<String, List<ArchivoProcesamientoDto>> archivosPorRegion,
            Long tipoEleccion,
            Path directorioBase) throws IOException {

        Map<String, String> zipsGenerados = new HashMap<>();
        String carpetaTipoEleccion = obtenerNombreCarpetaTipoEleccion(tipoEleccion);
        Path rutaBase = directorioBase.resolve(carpetaTipoEleccion);

        for (Map.Entry<String, List<ArchivoProcesamientoDto>> entry : archivosPorRegion.entrySet()) {
            String region = entry.getKey();
            Path rutaRegion = rutaBase.resolve(region);
            String nombreZip = carpetaTipoEleccion + "_" + region + ".zip";
            Path rutaZip = rutaRegion.resolve(nombreZip);

            // Eliminar ZIP anterior si existe
            eliminarZipAnterior(rutaZip);

            // Crear ZIP
            try (FileOutputStream fos = new FileOutputStream(rutaZip.toFile());
                    ZipOutputStream zos = new ZipOutputStream(fos)) {

                Path rutaActasyResoluciones = rutaRegion.resolve("ActasyResoluciones");
                File carpetaActasyResoluciones = rutaActasyResoluciones.toFile();

                // Verificar que la carpeta existe
                if (!carpetaActasyResoluciones.exists() || !carpetaActasyResoluciones.isDirectory()) {
                    log.warn("Carpeta ActasyResoluciones no existe para región: {}", region);
                    continue;
                }

                File[] archivos = carpetaActasyResoluciones.listFiles((dir, name) -> {
                    File file = new File(dir, name);
                    return file.isFile() && !name.endsWith(".zip");
                });

                if (archivos != null) {
                    for (File archivo : archivos) {
                        try (FileInputStream fis = new FileInputStream(archivo)) {
                            ZipEntry zipEntry = new ZipEntry(archivo.getName());
                            zos.putNextEntry(zipEntry);

                            byte[] buffer = new byte[8192];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                            zos.closeEntry();
                        }
                    }
                }

                zipsGenerados.put(region, rutaZip.toString());
                log.info("ZIP generado: {}", rutaZip);

                if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                    subirZipAS3(rutaZip.toFile(), carpetaTipoEleccion, region);
                }
            }

            // DUPLICAR ZIP CON TIMESTAMP
            duplicarZipConTimestamp(rutaZip.toString(), carpetaTipoEleccion, region, directorioBase);
        }

        return zipsGenerados;
    }

    private void subirZipAS3(File zipFile, String tipoEleccion, String region) {
        String key = tipoEleccion + "/" + region + "/" + zipFile.getName();
        try {
            s3Service.subirArchivoBucketReporte(key, zipFile);
            log.info("ZIP subido a S3: {}", key);
        } catch (Exception e) {
            log.error("Error subiendo ZIP a S3: {}", key, e);
        }
    }

    private void eliminarZipAnterior(Path rutaZip) {
        try {
            boolean eliminado = Files.deleteIfExists(rutaZip);
            if (eliminado) {
                log.info("ZIP eliminado: {}", rutaZip);
            }
        } catch (IOException e) {
            log.error("No se pudo eliminar el ZIP: {} - Error: {}", rutaZip, e.getMessage());
        }
    }

    @Override
    public Map<String, List<Map<String, String>>> listarZipsDisponibles(String tipoEleccionFiltro) {

        log.info("=== LISTANDO ZIPS DISPONIBLES ===");
        log.info("Tipo de elección filtro: {}", tipoEleccionFiltro != null ? tipoEleccionFiltro : "TODOS");
        log.info("Ruta base: {}", COP_NFS_PATH.concat(COP_PATH_FILES).concat(RUTA_BASE_DESTINO));

        boolean esNube = Boolean.TRUE.equals(Boolean.valueOf(despliegueNube));

        Map<String, List<Map<String, String>>> resultado = new LinkedHashMap<>();

        try {
            if (esNube) {
                log.info("Modo NUBE: consultando bucket de reportes");
                listarZipsDesdeS3(resultado, tipoEleccionFiltro);
            }else {

                File directorioBase = new File(COP_NFS_PATH.concat(COP_PATH_FILES).concat(RUTA_BASE_DESTINO));

                validarDirectorioBase(directorioBase, resultado);

                // Obtener carpetas de tipos de elección
                File[] carpetasTipoEleccion = directorioBase.listFiles(File::isDirectory);

                if (carpetasTipoEleccion == null || carpetasTipoEleccion.length == 0) {
                    log.warn("No se encontraron carpetas de tipos de elección en: {}",
                            COP_NFS_PATH.concat(COP_PATH_FILES).concat(RUTA_BASE_DESTINO));
                    return resultado;
                }

                // Filtrar por tipo de elección si se especificó
                List<File> carpetasFiltradas = Arrays.stream(carpetasTipoEleccion)
                        .filter(carpeta -> {
                            if (StringUtils.isEmpty(tipoEleccionFiltro)) {
                                return true; // Sin filtro, incluir todas
                            }
                            return carpeta.getName().equalsIgnoreCase(tipoEleccionFiltro);
                        })
                        .toList();

                log.info("Carpetas de tipos de elección encontradas: {}", carpetasFiltradas.size());

                // Procesar cada tipo de elección
                for (File carpetaTipoEleccion : carpetasFiltradas) {
                    String tipoEleccion = carpetaTipoEleccion.getName();
                    List<Map<String, String>> zipsDelTipo = new ArrayList<>();

                    log.info("Procesando tipo de elección: {}", tipoEleccion);

                    // Obtener carpetas de regiones
                    File[] carpetasRegion = carpetaTipoEleccion.listFiles(File::isDirectory);

                    if (carpetasRegion == null || carpetasRegion.length == 0) {
                        log.warn("No se encontraron carpetas de regiones en: {}", carpetaTipoEleccion.getAbsolutePath());
                        continue;
                    }

                    // Procesar cada región
                    for (File carpetaRegion : carpetasRegion) {
                        String region = carpetaRegion.getName();

                        // Buscar el archivo ZIP en la carpeta de la región
                        String nombreZip = tipoEleccion + "_" + region + ".zip";
                        File archivoZip = new File(carpetaRegion, nombreZip);

                        if (archivoZip.exists() && archivoZip.isFile()) {
                            Map<String, String> infoZip = new LinkedHashMap<>();
                            infoZip.put("tipoEleccion", tipoEleccion);
                            infoZip.put("region", region);
                            infoZip.put("nombreArchivo", nombreZip);
                            infoZip.put("ruta", archivoZip.getAbsolutePath());
                            infoZip.put("tamanio", formatearTamanio(archivoZip.length()));
                            infoZip.put("fechaModificacion",
                                    DateTimeUtil.dateToHoraFormatPR(new Date(archivoZip.lastModified())));
                            infoZip.put("urlDescarga",
                                    "/procesamientoActas/descargarZip?tipoEleccion=" + tipoEleccion + "&region=" + region);

                            zipsDelTipo.add(infoZip);

                            log.debug("ZIP encontrado: {} - {} ({} bytes)", tipoEleccion, region, archivoZip.length());
                        } else {
                            log.debug("ZIP no encontrado: {}", archivoZip.getAbsolutePath());
                        }
                    }

                    // Agregar al resultado solo si hay ZIPs
                    if (!zipsDelTipo.isEmpty()) {
                        resultado.put(tipoEleccion, zipsDelTipo);
                        log.info("Tipo de elección '{}': {} ZIPs encontrados", tipoEleccion, zipsDelTipo.size());
                    }
                }
            }
            log.info("=== TOTAL: {} tipos de elección con ZIPs ===", resultado.size());

        } catch (Exception e) {
            log.error("Error al listar ZIPs disponibles: {}", e.getMessage(), e);
        }

        return resultado;
    }

    private void listarZipsDesdeS3(Map<String, List<Map<String, String>>> resultado, String tipoEleccionFiltro) {
        try {
            List<String> objetos = s3Service.listarObjetosBucketReporte();

            // Agrupar por tipo de elección y región
            Map<String, List<Map<String, String>>> zipsPorTipo = new HashMap<>();

            for (String key : objetos) {
                if (!key.endsWith(".zip") || key.contains("/historicos/")) {
                    continue;
                }

                String relativePath = key.substring((COP_NFS_PATH + COP_PATH_FILES + "/").length());

                String[] partes = relativePath.split("/");
                if (partes.length < 3) continue;

                String tipoEleccion = partes[0];
                String region = partes[1];
                String nombreArchivo = partes[2];

                if (StringUtils.isNotEmpty(tipoEleccionFiltro) && !tipoEleccion.equalsIgnoreCase(tipoEleccionFiltro)) {
                    continue;
                }

                Map<String, String> infoZip = new LinkedHashMap<>();
                infoZip.put("tipoEleccion", tipoEleccion);
                infoZip.put("region", region);
                infoZip.put("nombreArchivo", nombreArchivo);
                infoZip.put("ruta", key);
                infoZip.put("tamanio", formatearTamanio(s3Service.obtenerTamanioObjeto(key)));
                infoZip.put("fechaModificacion", DateTimeUtil.dateToHoraFormatPR(s3Service.obtenerFechaModificacion(key)));
                infoZip.put("urlDescarga", "/procesamientoActas/descargarZip?tipoEleccion=" + tipoEleccion + "&region=" + region);

                zipsPorTipo.computeIfAbsent(tipoEleccion, k -> new ArrayList<>()).add(infoZip);
            }

            zipsPorTipo.forEach((tipo, lista) -> {
                lista.sort(Comparator.comparing(m -> m.get("region")));
                resultado.put(tipo, lista);
            });

        } catch (Exception e) {
            log.error("Error listando desde S3", e);
        }
    }

    private Map<String, List<Map<String, String>>> validarDirectorioBase(
            File directorioBase,
            Map<String, List<Map<String, String>>> resultado) {

        if (!directorioBase.exists() || !directorioBase.isDirectory()) {
            throw new IllegalStateException(
                    "Directorio base no existe o no es válido: " + directorioBase.getAbsolutePath());
        }
        return resultado;
    }

    @Override
    public Resource obtenerZipParaDescarga(String tipoEleccion, String region) {

        log.info("Obteniendo ZIP para descarga: {} - {}", tipoEleccion, region);

        try {
            // Construir ruta del archivo ZIP
            Path rutaZip = Paths.get(COP_NFS_PATH.concat(COP_PATH_FILES).concat(RUTA_BASE_DESTINO), tipoEleccion,
                    region, tipoEleccion + "_" + region + ".zip");
            File archivoZip = rutaZip.toFile();

            if (!archivoZip.exists() || !archivoZip.isFile()) {
                log.warn("Archivo ZIP no encontrado: {}", rutaZip);
                return null;
            }

            log.info("Archivo ZIP encontrado: {} ({} bytes)", rutaZip, archivoZip.length());

            return new FileSystemResource(archivoZip);

        } catch (Exception e) {
            log.error("Error al obtener ZIP para descarga: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Formatea el tamaño del archivo en formato legible
     */
    private String formatearTamanio(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Duplica un archivo ZIP agregando timestamp al nombre
     *
     * @param rutaZipOriginal     Ruta completa del ZIP original
     * @param carpetaTipoEleccion Nombre de la carpeta del tipo de elección
     * @param region              Nombre de la región
     * @param directorioBase      Directorio base donde se guardará la copia
     */
    private void duplicarZipConTimestamp(String rutaZipOriginal, String carpetaTipoEleccion,
            String region, Path directorioBase) {
        try {
            String timestamp = DateUtil.getFechaActual("ddMMyyyy_HHmmss");
            String nombreZipConTimestamp = carpetaTipoEleccion + "_" + region + "_" + timestamp + ".zip";
            Path rutaCarpetaHistoricosZip = directorioBase.resolve(carpetaTipoEleccion).resolve(region).resolve("HistoricosZip");

            // Crear carpeta de históricos si no existe
            Files.createDirectories(rutaCarpetaHistoricosZip);

            Path rutaZipDuplicado = rutaCarpetaHistoricosZip.resolve(nombreZipConTimestamp);

            Files.copy(Paths.get(rutaZipOriginal), rutaZipDuplicado, StandardCopyOption.REPLACE_EXISTING);

            if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                String key = carpetaTipoEleccion + "/" + region + "/historicos/" + nombreZipConTimestamp;
                s3Service.subirArchivoBucketReporte(key, rutaZipDuplicado.toFile());
            }

        } catch (IOException e) {
            log.error("Error al duplicar ZIP con timestamp para región {}: {}", region, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<TramaScePuestaCeroDto> eliminarCarpetaDescargaActas() {

        String rutaCarpeta = COP_NFS_PATH.concat(COP_PATH_FILES);
        Path carpeta = Paths.get(rutaCarpeta);

        try {
            if (Files.exists(carpeta)) {
                log.info("Limpiando contenido de carpeta: {}", rutaCarpeta);

                // Eliminar SOLO el contenido, mantiene la carpeta
                FileUtils.cleanDirectory(carpeta.toFile());

                log.info("Contenido de carpeta eliminado exitosamente: {}", rutaCarpeta);
            } else {
                log.error("La carpeta no existe: {}", rutaCarpeta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        TramaScePuestaCeroDto.builder()
                                .mensaje("La carpeta no existe: " + rutaCarpeta)
                                .limpiarCarpetaDescargaActas(false)
                                .build());

            }

            // Eliminar registros en las tablas relacionadas
            log.info("Eliminando colecciones de la base de datos secundaria...");
            secondaryMongoTemplate
                    .dropCollection(pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabArchivo.class);
            secondaryMongoTemplate.dropCollection(TabReporteAutomatico.class);
            secondaryMongoTemplate.dropCollection(TabReporte.class);
            secondaryMongoTemplate.dropCollection(TabCronReporteActas.class);
            log.info("Colecciones de la base de datos secundaria eliminadas exitosamente.");

            log.info("Registros relacionados eliminados exitosamente");

        } catch (IOException e) {
            log.error("Error al limpiar carpeta: {}", rutaCarpeta, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    TramaScePuestaCeroDto.builder()
                            .mensaje("Error al limpiar la carpeta " + rutaCarpeta)
                            .limpiarCarpetaDescargaActas(false)
                            .build());
        }

        return ResponseEntity.ok(
                TramaScePuestaCeroDto.builder()
                        .mensaje("Contenido de carpeta eliminado correctamente")
                        .limpiarCarpetaDescargaActas(true)
                        .build());
    }

    @Override
    public ResponseEntity<ReporteCronResponse> generarReporteManual(String idConfiguracion) {
        GenericResponse<String> response = new GenericResponse<>();
        Optional<TabCronReporteActas> configOpt = tabProgramacionReporteRepository.findById(idConfiguracion);
        if (configOpt.isPresent()) {

            TabCronReporteActas taskConfig = configOpt.get();
            Double porcentajeContabilizadasActual = validarPorcentajeService
                    .obtenerPorcentageContabilizado(taskConfig.getEleccionId());
            if (porcentajeContabilizadasActual == 100) {
                procesarActas(idConfiguracion);
                response.setMessage("Se esta procesando su reporte manual.");
                response.setSuccess(true);
            } else {
                response.setMessage("El reporte solo puede generarse cuando las actas contabilizadas estén al 100%");
                response.setSuccess(false);
            }

        }

        return new ResponseEntity<>(ReporteCronResponse.builder().response(response).build(), HttpStatus.OK);

    }

}