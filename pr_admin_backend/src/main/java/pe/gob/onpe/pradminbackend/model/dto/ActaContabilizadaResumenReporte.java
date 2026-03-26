package pe.gob.onpe.pradminbackend.model.dto;

import java.util.List;

import lombok.Data;
import pe.gob.onpe.pradminbackend.model.bd.service.impl.ActasPorProcesarDto;
import pe.gob.onpe.pradminbackend.model.bd.service.impl.ActasProcesadasDto;

@Data
public class ActaContabilizadaResumenReporte {

	private EncabezadoFiltroContabilizacionActa encabezado;
	private List<ActaContabilizadaReporte> detalleValidos;
	private ActaContabilizadaReporte votosValidos;
	private ActaContabilizadaReporte votosEmitidos;
	private List<ActaContabilizadaReporte> detalleNoValidos;
	private MesasAInstalarDto resumenMesasAInstalar;
	private ActasProcesadasDto resumenActasProcesadas;
	private ActasPorResolverJEEDto resumenActasPorResolverJEE;
	private ActasAnuladasPorResolucionDto resumenActasAnuladasPorResolucion;
	private ActasPorProcesarDto resumenActasPorProcesar;
	
	
}


