package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.TabReporteAutomaticoReqDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;

import java.util.List;

public interface TabReporteAutomaticoService extends CrudService<TabReporteAutomatico> {
	
	List<TabReporteAutomaticoResDto> obtenerTodos();
	TabReporteAutomaticoResDto obtenerPorId(String id);
	TabReporteAutomaticoResDto crear(TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto);
	TabReporteAutomaticoResDto actualizar(String id, TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto);
}
