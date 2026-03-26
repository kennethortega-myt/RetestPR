package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.dto.ActaContabilizadaResumenReporte;
import pe.gob.onpe.pradminbackend.model.dto.FiltroContabilizacionActa;

public interface ContabilizacionVotosService {

	public ActaContabilizadaResumenReporte contabilizarVotosPorMesa(FiltroContabilizacionActa filtro);

	public byte[] getReporteContabilizarVotosPorMesa(FiltroContabilizacionActa filtro);
	
}
