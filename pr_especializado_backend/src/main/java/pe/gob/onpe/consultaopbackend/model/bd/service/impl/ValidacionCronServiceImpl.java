package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabCronReporteActas;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabProgramacionReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteActasRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.ValidacionCronService;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidacionCronServiceImpl implements ValidacionCronService {

    private final TabProgramacionReporteRepository tabProgramacionReporteRepository;
    private final TabReporteActasRepository tabReporteActasRepository;

    /**
     * Actualiza el estado de ejecucion del cron reporte automatizado
     *
     */
    @Override
    public void actualizarEstadoReporteAutomaticoCronEjecucion(TabReporteAutomatico cron, TipoEstadoProcesoEnum procesoEnum) {
        cron.setEstadoProceso(procesoEnum.getCodigo().intValue());
        tabProgramacionReporteRepository.save(cron);
        log.info("Actualizando estado ejecucion cron reporte automatizado {}",
                procesoEnum.getDescripcion());
    }

    /**
     * Actualiza el estado de ejecucion del cron reporte actas
     *
     */
    @Override
    public void actualizarEstadoReporteActasCronEjecucion(TabCronReporteActas cron, TipoEstadoProcesoEnum procesoEnum) {
        cron.setEstadoProceso(procesoEnum.getCodigo().intValue());
        tabReporteActasRepository.save(cron);
        log.info("Actualizando estado ejecucion cron reporte actas {}",
                procesoEnum.getDescripcion());
    }
}
