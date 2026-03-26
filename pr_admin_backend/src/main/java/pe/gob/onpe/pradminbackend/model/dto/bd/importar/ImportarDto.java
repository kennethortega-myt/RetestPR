package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImportarDto {
	private List<ProcesoElectoralDto> proceso;
	private List<EleccionDto> eleccion;
	private List<UbigeoDto> ubigeo;
	private List<AgrupacionPoliticaDto> agrupacionPolitica;
	private List<UbigeoEleccionDto> ubigeoEleccion;
	private List<DetUbigeoEleccionAgrupacionPoliticaDto> ubigeoEleccionAgrupacionPolitica;
	private List<DistritoElectoralDto> distritoElectoral;
	private List<CatalogoDto> catalogo;
	private List<DetCatalogoEstructuraDto> catalogoEstructura;
	private List<DetCatalogoReferenciaDto> catalogoReferencia;
	private VistaEleccionDto vistasEleccion;
	private List<VwPrActaDto> vistaActa;
	private List<VwPrParticipacionCiudadanaDto> vistaParticipacionCiudadano;
	private List<LocalVotacionDto> localVotacion;
	private List<VwPrMesaDto> vistaMesa;
	private List<CandidatoDto> candidato;
	private List<VwPrTotalCandidatosPorAgrupacionPoliticaExportDto> vistaTotalCandidatosPorAgrupacionPoliticaExportDto;
}
