package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;

import java.util.List;

public interface TabReporteAutomaticoService extends CrudService<TabReporteAutomatico> {
	
	List<TabReporteAutomaticoResDto> obtenerTodos();
	TabReporteAutomaticoResDto obtenerPorId(String id);
	TabReporteAutomaticoResDto crear(TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto);
	TabReporteAutomaticoResDto actualizar(String id, TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto);
	void Eliminar(String id);
}
