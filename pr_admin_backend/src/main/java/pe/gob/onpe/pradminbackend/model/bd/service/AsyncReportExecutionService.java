package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;

public interface AsyncReportExecutionService {
    void executeReportTask(TabReporteAutomatico taskConfig);
}
