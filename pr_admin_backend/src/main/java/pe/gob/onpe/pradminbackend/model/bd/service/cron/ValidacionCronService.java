package pe.gob.onpe.pradminbackend.model.bd.service.cron;

import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;

public interface ValidacionCronService {

    void actualizarEstadoReporteAutomaticoCronEjecucion(TabReporteAutomatico cron, TipoEstadoProcesoEnum procesoEnum);

}