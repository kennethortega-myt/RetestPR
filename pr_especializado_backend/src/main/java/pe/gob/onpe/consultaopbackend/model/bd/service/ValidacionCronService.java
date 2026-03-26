package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabCronReporteActas;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

public interface ValidacionCronService {

    void actualizarEstadoReporteAutomaticoCronEjecucion(TabReporteAutomatico cron, TipoEstadoProcesoEnum procesoEnum);
    void actualizarEstadoReporteActasCronEjecucion(TabCronReporteActas cron, TipoEstadoProcesoEnum procesoEnum);

}