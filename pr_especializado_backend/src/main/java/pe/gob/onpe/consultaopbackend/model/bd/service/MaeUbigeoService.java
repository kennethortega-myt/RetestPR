package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.consultaopbackend.model.dto.*;

import java.util.List;

public interface MaeUbigeoService extends CrudService<MaeUbigeo> {

	public List<String> findDistinctDepartamentos();

	public List<UbigeoLocalVotacionDto> listarLocalVotacionPorIdEleccion(FiltroUbigeoLocalVotacionDto filtro);

	public List<UbigeoDepartamentoDto> listarDepartamentosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro);
	public List<UbigeoProvinciaDto>  listarProvinciasPorIdEleccionII(FiltroUbigeoProvinciaDto filtro);
	public List<UbigeoDistritoDto> listarDistritosPorIdEleccionII(FiltroUbigeoDistritoDto filtro);
	public List<UbigeoDistritoDto> listarDepProvDistritosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro);
}
