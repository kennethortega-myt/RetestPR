package pe.gob.onpe.consultaopcron.model.bd.service;


import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteActa;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopcron.utils.enums.TipoEstadoProcesoEnum;

public interface ValidacionCronService {

    void actualizarEstadoReporteAutomaticoCronEjecucion(TabReporteAutomatico cron, TipoEstadoProcesoEnum procesoEnum);
    void actualizarEstadoReporteActasCronEjecucion(TabReporteActa cron, TipoEstadoProcesoEnum procesoEnum);

}