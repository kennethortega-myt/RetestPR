package pe.gob.onpe.pradminbackend.model.bd.service.reportes.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeFechaRepository;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabArchivoReporteRepository;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteAutomaticoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.cron.impl.ValidacionCronServiceImpl;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.TabArchivoReporteService;
import pe.gob.onpe.pradminbackend.nfs.FileApp;
import pe.gob.onpe.pradminbackend.nfs.NfsService;
import pe.gob.onpe.pradminbackend.s3.S3Service;
import pe.gob.onpe.pradminbackend.utils.ArchivoUtils;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionMayusculaEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;

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
    private final TabReporteAutomaticoRepository tabProgramacionReporteRepository;
    private final ValidacionCronServiceImpl validacionCronService;
    private final S3Service s3Service;

    @Value("${despliegue-nube}")
    private String despliegueNube;

    @Override
	@Transactional
    public TabArchivo guardarArchivoReporte(TabReporte reporte, byte[] repote, String path, String formato) {
		TabArchivo archivo = new TabArchivo();
        String eleccion = TipoEleccionMayusculaEnum.obtenerDescripcion(Long.valueOf(reporte.getTipoEleccion()));
        String uuid = UUID.randomUUID().toString();

        try {

            String mimetype= "";

            if(formato.equals("xlsx")) {
                mimetype = "application/vnd.ms-excel";
            } else if(formato.equals("zip")) {
                mimetype = "application/zip";
            } else if(formato.equals("csv")) {
                mimetype = "text/csv";
            }

            Double porcentajeAvance = reporte.getPorcentaje();

            Date fechaProceso = maeFechaRepository.findAll().getFirst().getFechaProceso();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-a");
            String fechaFormateada = sdf.format(fechaProceso);

            String filename = "PR_" + eleccion + "_" + fechaFormateada + "_" + porcentajeAvance;
            String filenameExt = filename+"."+formato;
            String ruta = path + "/"+"reportes" + "/" + TipoEleccionMayusculaEnum
                    .obtenerDescripcion(Long.valueOf(reporte.getTipoEleccion()))
                    .replace(" ","_");

            this.nfsService.upload(
                    new FileApp(repote,filenameExt),ruta);

            if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                s3Service.copiarReporteDesdeEfsAS3(filenameExt, ruta);
            }

            log.info("Guardando archivo: {}", filenameExt);
            log.info("Ruta: {}", ruta + "/" + filenameExt);
            archivo.setId(uuid);
            archivo.setDFechaProceso(fechaProceso);
            archivo.setCGuid(uuid);
            archivo.setCPeso(ArchivoUtils.formatBytes(repote.length));
            archivo.setCNombre(filename);
            archivo.setCNombreOriginal(filenameExt);
            archivo.setCRuta(ruta + "/" + filenameExt);
            archivo.setCFormato(mimetype);
            archivo.setNActivo(1);
            archivo.setDAudFechaCreacion(new Date());
            archivo.setCAudUsuarioCreacion("siscop");
            archivoReporteRepository.save(archivo);

            //Actualizar porcentaje contabilizadas
            tabProgramacionReporteRepository.findAll().forEach(taskConfig -> {
                if(taskConfig.getEleccionId().intValue() == reporte.getTipoEleccion()) {
                    taskConfig.setPorcentaje(porcentajeAvance);
                    tabProgramacionReporteRepository.save(taskConfig);
                    validacionCronService.actualizarEstadoReporteAutomaticoCronEjecucion(taskConfig,
                            TipoEstadoProcesoEnum.PENDIENTE);
                    log.info("Porcentaje de contabilizadas actualizado");
                }
            });

        } catch (Exception e) {
            log.error("Error al guardar el reporte: ", e);
            return null;
        }

        return archivo;
		
	}

	@Override
	public Optional<TabArchivo> getArchivoById(String idArchivo) {
		return this.archivoReporteRepository.findById(idArchivo);
	}
	
	

}
