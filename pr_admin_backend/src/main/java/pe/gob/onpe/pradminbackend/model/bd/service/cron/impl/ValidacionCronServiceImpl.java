package pe.gob.onpe.pradminbackend.model.bd.service.cron.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteAutomaticoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.cron.ValidacionCronService;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;


@Slf4j
@Service
@RequiredArgsConstructor
public class ValidacionCronServiceImpl implements ValidacionCronService {

    private final TabReporteAutomaticoRepository tabProgramacionReporteRepository;

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

}
