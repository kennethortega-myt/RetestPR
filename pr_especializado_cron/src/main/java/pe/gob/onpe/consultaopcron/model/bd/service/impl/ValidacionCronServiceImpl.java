package pe.gob.onpe.consultaopcron.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteActa;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopcron.model.bd.repository.secondary.TabProgramacionReporteActasRepository;
import pe.gob.onpe.consultaopcron.model.bd.repository.secondary.TabProgramacionReporteRepository;
import pe.gob.onpe.consultaopcron.model.bd.service.ValidacionCronService;
import pe.gob.onpe.consultaopcron.utils.enums.TipoEstadoProcesoEnum;


@Slf4j
@Service
@RequiredArgsConstructor
public class ValidacionCronServiceImpl implements ValidacionCronService {

    private final TabProgramacionReporteRepository tabProgramacionReporteRepository;
    private final TabProgramacionReporteActasRepository tabReporteActasRepository;

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
    public void actualizarEstadoReporteActasCronEjecucion(TabReporteActa cron, TipoEstadoProcesoEnum procesoEnum) {
        cron.setEstadoProceso(procesoEnum.getCodigo().intValue());
        tabReporteActasRepository.save(cron);
        log.info("Actualizando estado ejecucion cron reporte actas {}",
                procesoEnum.getDescripcion());
    }
}
