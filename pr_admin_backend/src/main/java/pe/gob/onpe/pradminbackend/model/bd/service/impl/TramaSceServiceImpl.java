package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeImportarRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaePuestaCeroRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.*;
import pe.gob.onpe.pradminbackend.model.dto.mapper.UtilMapper;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.*;
import pe.gob.onpe.pradminbackend.mqlistener.ColaService;
import pe.gob.onpe.pradminbackend.nfs.NfsService;
import pe.gob.onpe.pradminbackend.s3.S3Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TramaSceServiceImpl implements TramaSceService {

    private final ParticipacionCiudadanaService participacionCiudadanaService;
    private final ActaService actaService;
    private final VwPrDiputadosService diputadosService;
    private final VwPrSenadoresDistritoElectoralMultipleService senadoresDistritoElectoralMultipleService;
    private final VwPrSenadoresDistritoNacionalUnicoService senadoresDistritoNacionalUnicoService;
    private final VwPrParlamentoAndinoService parlamentoAndinoService;
    private final VwPrPresidencialesService presidencialesService;
    private final VwPrRevocatoriaDistritalService revocatoriaDistritalService;
    private final VwPrMesaService mesaService;
    private final MaeFechaService maeFechaService;

    private final MongoTemplate mongoTemplate;
    private final MongoTemplate secondaryMongoTemplate;
    private final MaePuestaCeroRepository maePuestaCeroRepository;
    private final MaeImportarRepository maeImportarRepository;
    private final NfsService nfsService;
    private final ColaService colaService;
    private final S3Service s3Service;

    private static final String VW_PR_PARTICIPACION_CIUDADANA = "vw_pr_participacion_ciudadana";
    private static final String VW_PR_ACTA = "vw_pr_acta";
    private static final String VW_PR_MESA = "vw_pr_mesa";

    @Value("${despliegue-nube}")
    private String despliegueNube;

    public TramaSceServiceImpl(
            ParticipacionCiudadanaService participacionCiudadanaService,
            ActaService actaService,
            VwPrDiputadosService diputadosService,
            VwPrSenadoresDistritoElectoralMultipleService senadoresDistritoElectoralMultipleService,
            VwPrSenadoresDistritoNacionalUnicoService senadoresDistritoNacionalUnicoService,
            VwPrParlamentoAndinoService parlamentoAndinoService,
            VwPrPresidencialesService presidencialesService,
            VwPrRevocatoriaDistritalService revocatoriaDistritalService,
            VwPrMesaService mesaService,
            MaeFechaService maeFechaService,
            MongoTemplate mongoTemplate,
            @Qualifier("secondaryMongoTemplate") MongoTemplate secondaryMongoTemplate,
            MaePuestaCeroRepository maePuestaCeroRepository,
            MaeImportarRepository maeImportarRepository,
            NfsService nfsService,
            ColaService colaService,
            S3Service s3Service) {
        this.participacionCiudadanaService = participacionCiudadanaService;
        this.actaService = actaService;
        this.diputadosService = diputadosService;
        this.senadoresDistritoElectoralMultipleService = senadoresDistritoElectoralMultipleService;
        this.senadoresDistritoNacionalUnicoService = senadoresDistritoNacionalUnicoService;
        this.parlamentoAndinoService = parlamentoAndinoService;
        this.presidencialesService = presidencialesService;
        this.revocatoriaDistritalService = revocatoriaDistritalService;
        this.mesaService = mesaService;
        this.maeFechaService = maeFechaService;
        this.mongoTemplate = mongoTemplate;
        this.secondaryMongoTemplate = secondaryMongoTemplate;
        this.maePuestaCeroRepository = maePuestaCeroRepository;
        this.maeImportarRepository = maeImportarRepository;
        this.nfsService = nfsService;
        this.colaService = colaService;
        this.s3Service = s3Service;
    }
    
    @Override
    public Optional<TramaVistaResponse> recibirTrama(List<TramaSceDto> listTramaSce) {

    	log.info("se ejecuto el metodo recibirTrama!");
        List<TramaVistaDetalleResponse> detalleRecibido = new ArrayList<>();
        listTramaSce.forEach(trama -> {
        	
        	try {
        		log.info("Trama recibido en PR: {} ", trama);
                boolean recibido = false;
                List<TramaVistaFilaResponse> filas = new ArrayList<>();
                switch (trama.getVista()) {
                    case VW_PR_PARTICIPACION_CIUDADANA -> {
                        List<VwPrParticipacionCiudadana> listaParticipacionActualizar = trama.getTramaParticipacion().stream()
                                .map(UtilMapper::convertirParticipacionCiudadanaParaTransmision).toList();
                        filas = participacionCiudadanaService.actualizarParticipacion(listaParticipacionActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case VW_PR_ACTA -> {
                        List<VwPrActa> listaActaActualizar = trama.getTramaActa().stream()
                                .map(UtilMapper::convertirActaParaTransmision).toList();
                        filas = actaService.actualizarActas(listaActaActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_diputados" -> {
                        List<VwPrDiputados> listaActaActualizar = trama.getTramaDiputados().stream()
                                .map(UtilMapper::convertirEleccionDiputadosParaTransmision).toList();
                        filas = diputadosService.actualizarEleccionDiputado(listaActaActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_senadores_distrito_multiple" -> {
                        List<VwPrSenadoresDistritoElectoralMultiple> listaSenadoresMultipleActualizar = trama.getTramaSenadoresDistritoElectoralMultiple().stream()
                                .map(UtilMapper::convertirEleccionSenadoresDistritoElectoralMultipleParaTransmision).toList();
                        filas = senadoresDistritoElectoralMultipleService.actualizarDistritoElectoralMultiple(listaSenadoresMultipleActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_senadores_distrito_unico" -> {
                        List<VwPrSenadoresDistritoNacionalUnico> listaSenadoresUnicoActualizar = trama.getTramaSenadoresDistritoNacionalUnico().stream()
                                .map(UtilMapper::convertirSenadoresDistritoNacionalUnicoParaTransmision).toList();
                        filas = senadoresDistritoNacionalUnicoService.actualizarDistritoNacionalUnico(listaSenadoresUnicoActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_parlamento_andino" -> {
                        List<VwPrParlamentoAndino> listaParlamentoActualizar = trama.getTramaParlamento().stream()
                                .map(UtilMapper::convertirParlamentoAndinoParaTransmision).toList();
                        filas = parlamentoAndinoService.actualizarParlamentoAndino(listaParlamentoActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_presidente_y_vicepresidentes" -> {
                        List<VwPrPresidenciales> listaPresidencialActualizar = trama.getTramaPresidenciales().stream()
                                .map(UtilMapper::convertirEleccionPresidencialParaTransmision).toList();
                        filas = presidencialesService.actualizarEleccionPresidencial(listaPresidencialActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case VW_PR_MESA -> {
                        List<VwPrMesa> listaMesaActualizar = trama.getTramaMesa().stream()
                                .map(UtilMapper::convertirVistaMesaParaTransmision).toList();
                        filas = mesaService.actualizarMesas(listaMesaActualizar, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    case "vw_pr_revocatoria_distrital" -> {
                        List<VwPrRevocatoriaDistrital> listaRevocatoriaDistrital = trama.getTramaRevocatoriaDistrital().stream()
                                .map(UtilMapper::convertirRevocatoriaDistritalParaTransmision).toList();
                        filas = revocatoriaDistritalService.actualizarRevocatoriaDistrital(listaRevocatoriaDistrital, trama.getIdActa(), trama.getUsuario());
                        recibido = filas.stream().allMatch(TramaVistaFilaResponse::isRecibido);
                    }
                    default -> log.info("Nombre de vista no mapeado en PR: {}", trama.getVista());
                }
                
                detalleRecibido.add(TramaVistaDetalleResponse.builder()
                        .idTransferencia(trama.getIdTransferencia())
                        .filas(filas)
                        .mensaje("Se registro correctamente")
                        .recibido(recibido).build());
                
        	} catch(Exception e) {
                log.error("error en recibirTrama: ", e);
        		detalleRecibido.add(TramaVistaDetalleResponse.builder()
                        .idTransferencia(trama.getIdTransferencia())
                        .filas(null)
                        .mensaje(e.getMessage())
                        .recibido(false).build());
        		
        	}
        });


        Optional<MaeFecha> fecha = maeFechaService.findById(1);
        Date fechaRegistroMaeFecha = new Date();
        log.info("======TramaSceServiceImpl: Esta es la fecha que se regsitra en maefecha {} =======", fechaRegistroMaeFecha);

        if (fecha.isPresent()) {
            MaeFecha registro = fecha.get();
            registro.setFechaProceso(fechaRegistroMaeFecha);
            maeFechaService.save(registro);
        } else {
            MaeFecha obj = MaeFecha.builder()
                    .id(1).activo(1).audFechaCreacion(new Date())
                    .servicioFirma(true)
                    .audUsuarioCreacion("usuario-sce")
                    .cDescripcion("Ultima Fecha Transmision")
                    .fechaProceso(fechaRegistroMaeFecha)
                    .build();
            maeFechaService.save(obj);
        }


        return Optional.of(TramaVistaResponse.builder().detalle(detalleRecibido).build());
    }

    @Override
    public TramaScePuestaCeroDto puestaCero(String usuarioSce) {
        TramaScePuestaCeroDto respuesta = TramaScePuestaCeroDto.builder().build();
        try {
            log.error("PUESTA CERO INICIO");

            MaePuestaCero registro = new MaePuestaCero();

            if (Boolean.FALSE.equals(Boolean.valueOf(despliegueNube))) {

                //llamar puesta cero nube
                nfsService.puestaCeroNube();

                registro = maePuestaCeroRepository.save(MaePuestaCero.builder()
                        .usuarioSce(usuarioSce)
                        .fechaInicio(new Date())
                        .audFechaCreacion(new Date())
                        .audUsuarioCreacion("admin-pr")
                        .build());

                List<MaeImportar> dataActualizar = maeImportarRepository.findAll().stream().map(data -> {
                    data.setExito(false);
                    return data;
                }).toList();
                maeImportarRepository.saveAll(dataActualizar);
                respuesta.setActualizarMaeImportar(true);
                log.info("Se actualiza mae_importar");

                getColeccionesGeneralesPR().forEach(this::eliminarColeccion);
                log.info("Se elimina colecciones en BD PR");
                respuesta.setLimpiarColeccionesPR(true);
            }

            getColeccionesCiudadanoReporte().forEach(this::eliminarColeccionSecondary);
            log.info("Se elimina colecciones en BD PR Secondary");
            nfsService.eliminarTodosLosArchivos();
            log.info("Se elimina archivos del NFS");
            respuesta.setLimpiarArchivosNFS(true);

            if (Boolean.TRUE.equals(Boolean.valueOf(despliegueNube))) {
                eliminarColeccion("tab_reporte_candidato");

                s3Service.limpiarBucket();
                log.info("Se limpio bucket s3");
            }

            // Eliminar carpeta descarga-actas
            respuesta.setLimpiarCarpetaDescargaActas(
                    nfsService.eliminarCarpetaDescargaActas());

            //Eliminar boletines
            respuesta.setLimpiarBoletines(
                    nfsService.eliminarBoletines());

            if (Boolean.FALSE.equals(Boolean.valueOf(despliegueNube))) {
                colaService.eliminarMensajesCola();
                log.info("Se elimina mensajes de la cola MQ");
                respuesta.setLimpiarMensajesColaMQ(true);
                maePuestaCeroRepository.findById(registro.getId()).ifPresent(elemento -> {
                    elemento.setFechaFin(new Date());
                    elemento.setAudFechaModificacion(new Date());
                    elemento.setAudUsuarioModificacion("admin-pr");
                    maePuestaCeroRepository.save(elemento);
                });
            }

            log.info("PUESTA CERO FIN");
        } catch (Exception error) {
            log.error("Error en puesta cero: ", error);
            respuesta.setPuestaCero(false);
            respuesta.setMensaje(error.getMessage());
            return respuesta;
        }
        respuesta.setPuestaCero(true);
        respuesta.setMensaje("éxito");

        return respuesta;

    }

    private void eliminarColeccion(String nombreColeccion) {
        if (mongoTemplate.collectionExists(nombreColeccion)) {
            mongoTemplate.dropCollection(nombreColeccion);
            mongoTemplate.createCollection(nombreColeccion);
        }else {
            mongoTemplate.createCollection(nombreColeccion);
        }
    }

    private void eliminarColeccionSecondary(String nombreColeccion) {
        if (secondaryMongoTemplate.collectionExists(nombreColeccion)) {
            secondaryMongoTemplate.dropCollection(nombreColeccion);
        }
    }

    private List<String> getColeccionesCiudadanoReporte(){

        return List.of(
                "tab_archivo",
                "tab_reporte",
                "tab_reporte_automatico",
                "tab_reporte_candidato"
        );
    }

    private List<String> getColeccionesGeneralesPR(){
        return List.of(
                "cab_catalogo",
                "det_catalogo_estructura",
                "det_catalogo_referencia",
                "det_ubigeo_eleccion",
                "det_ubigeo_eleccion_agrupacion_politica",
                "mae_agrupacion_politica",
                "mae_candidato",
                "mae_eleccion",
                "mae_fecha",
                "mae_local_votacion",
                "mae_proceso_electoral",
                "mae_ubigeo",
                VW_PR_ACTA,
                VW_PR_MESA,
                VW_PR_PARTICIPACION_CIUDADANA,
                "vw_pr_presidente_y_vicepresidentes",
                "vw_pr_senadores_distrito_multiple",
                "vw_pr_senadores_distrito_unico",
                "vw_pr_diputados",
                "vw_pr_parlamento_andino",
                "vw_total_candidatos_por_agrupacion_politica",
                "mae_distrito_electoral",
                "tab_archivo",
                "tab_reporte_candidato"
        );
    }

}
