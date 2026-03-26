package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.presentacionbackend.model.dto.*;

public interface MaeUbigeoService extends CrudService<MaeUbigeo> {

	List<UbigeoDepartamentoDto> listarDepartamentosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro);
	List<UbigeoProvinciaDto>  listarProvinciasPorIdEleccionII(FiltroUbigeoProvinciaDto filtro);
	List<UbigeoDistritoDto> listarDistritosPorIdEleccionII(FiltroUbigeoDistritoDto filtro);
	List<UbigeoLocalVotacionDto> listarLocalVotacionPorIdEleccion(FiltroUbigeoLocalVotacionDto filtro);
	List<UbigeoDistritoDto> listarDepProvDistritosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro);

}
