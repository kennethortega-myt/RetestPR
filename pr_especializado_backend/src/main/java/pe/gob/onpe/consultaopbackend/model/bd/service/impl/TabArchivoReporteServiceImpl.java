package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeFechaRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.TabArchivoReporteCandidatoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabArchivoReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabProgramacionReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabArchivoReporteService;
import pe.gob.onpe.consultaopbackend.nfs.FileApp;
import pe.gob.onpe.consultaopbackend.nfs.NfsService;
import pe.gob.onpe.consultaopbackend.s3.S3Service;
import pe.gob.onpe.consultaopbackend.utils.ArchivoUtils;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TabArchivoReporteServiceImpl implements TabArchivoReporteService {

    private final TabArchivoReporteRepository archivoReporteRepository;
    private final NfsService nfsService;
    private final MaeFechaRepository maeFechaRepository;
    private final TabProgramacionReporteRepository tabProgramacionReporteRepository;
    private final TabArchivoReporteCandidatoRepository tabArchivoReporteCandidatoRepository;
    private final S3Service s3Service;

    @Value("${despliegue-nube}")
    private String despliegueNube;

    @Override
    @Transactional
    public TabArchivo guardarArchivoReporte(TabReporte reporte, byte[] repote, String path, String formato) {
        TabArchivo archivo = new TabArchivo();
        log.info("TIPO ELECCION ::::: " + reporte.getTipoEleccion());
        log.info("PORCENTAJE:::::: " + reporte.getPorcentaje());
        String eleccion = TipoEleccionEnum.obtenerDescripcion(Long.valueOf(reporte.getTipoEleccion()));
        String uuid = UUID.randomUUID().toString();

        String mimetype = "";
        if (formato.equals("xlsx")) {
            mimetype = "application/vnd.ms-excel";
        } else if (formato.equals("zip")) {
            mimetype = "application/zip";
        } else if (formato.equals("csv")) {
            mimetype = "text/csv";
        }
        Date fechaProceso = maeFechaRepository.findAll().getFirst().getFechaProceso();
        Double porcentajeAvance = reporte.getPorcentaje();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-a");
        String fechaFormateada = sdf.format(fechaProceso);

        String filename = "PR-ESP_" + eleccion + "_" + fechaFormateada + "_" + porcentajeAvance;
        String filenameExt = filename + "." + formato;
        String ruta = path + "/" + filenameExt;

        archivo.setId(uuid);
        archivo.setCGuid(uuid);
        archivo.setDFechaProceso(fechaProceso);
        archivo.setCPeso(ArchivoUtils.formatBytes(repote.length));
        archivo.setCNombre(filename);
        archivo.setCNombreOriginal(filenameExt);
        archivo.setCRuta(ruta);
        archivo.setCFormato(mimetype);
        archivo.setNActivo(1);
        archivo.setDAudFechaCreacion(new Date());
        archivo.setCAudUsuarioCreacion("siscop");

        try {
            this.nfsService.upload(new FileApp(repote, filenameExt), path);

            if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                s3Service.copiarReporteDesdeEfsAS3(filenameExt, path);
            }
        } catch (Exception e) {
            log.error("Error en guardarArchivoExcel: ", e);
            return null;
        }

        archivoReporteRepository.save(archivo);

        // Actualizar porcentaje contabilizadas
        tabProgramacionReporteRepository.findAll().forEach(taskConfig -> {
            if (taskConfig.getEleccionId().intValue() == reporte.getTipoEleccion()) {
                taskConfig.setPorcentaje(porcentajeAvance);
                tabProgramacionReporteRepository.save(taskConfig);
                log.info("Porcentaje de contabilizadas actualizado");
            }
        });

        return archivo;

    }

    @Override
    public Optional<TabArchivo> getArchivoById(String idArchivo) {
        return this.archivoReporteRepository.findById(idArchivo);
    }

    @Override
    public Optional<TabReporteCandidato> findByIdAndActivo(Integer codigoTipoEleccion, Integer activo) {
        return this.tabArchivoReporteCandidatoRepository.findByIdAndActivo(codigoTipoEleccion, activo);
    }
}
