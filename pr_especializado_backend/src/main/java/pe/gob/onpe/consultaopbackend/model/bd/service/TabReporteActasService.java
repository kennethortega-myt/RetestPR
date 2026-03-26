package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabCronReporteActas;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasResDto;

import java.util.List;

public interface TabReporteActasService extends CrudService<TabCronReporteActas> {
	
	List<TabReporteActasResDto> obtenerTodos();
    TabReporteActasResDto obtenerPorId(String id);
    TabReporteActasResDto crear(TabReporteActasReqDto tabReporteActasReqDto);
    TabReporteActasResDto actualizar(TabReporteActasReqDto tabReporteActasReqDto);
	void Eliminar(String id);
}
