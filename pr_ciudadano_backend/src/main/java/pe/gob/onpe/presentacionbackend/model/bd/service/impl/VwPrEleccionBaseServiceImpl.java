package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrEleccionBase;

public class VwPrEleccionBaseServiceImpl {

	private VwPrEleccionBaseServiceImpl() {
		throw new IllegalStateException("VwPrEleccionBaseServiceImpl class");
	}
	public static void mapeoCamposActualizar(VwPrEleccionBase registroNuevo, VwPrEleccionBase registroActual) {
		if (registroNuevo.getTotalElectoresHabiles() != null) {
			registroActual.setTotalElectoresHabiles(registroNuevo.getTotalElectoresHabiles());
		}
		if (registroNuevo.getTotalActas() != null ) {
			registroActual.setTotalActas(registroNuevo.getTotalActas());
		}

		if (registroNuevo.getTotalVotosEmitidos() != null && registroNuevo.getTotalVotosEmitidos().compareTo(0) != 0) {
			registroActual.setTotalVotosEmitidos(registroNuevo.getTotalVotosEmitidos());
		}
		if (registroNuevo.getTotalVotosValidos() != null && registroNuevo.getTotalVotosValidos().compareTo(0) != 0 ) {
			registroActual.setTotalVotosValidos(registroNuevo.getTotalVotosValidos());
		}
		
		if (registroNuevo.getParticipacionCiudadana() != null ) {
			registroActual.setParticipacionCiudadana(registroNuevo.getParticipacionCiudadana());
		}
		if (registroNuevo.getPorcentajeParticipacionCiudadana() != null ) {
			registroActual.setPorcentajeParticipacionCiudadana(registroNuevo.getPorcentajeParticipacionCiudadana());
		}
		
		if (registroNuevo.getActasContabilizadas() != null ) {
			registroActual.setActasContabilizadas(registroNuevo.getActasContabilizadas());
		}
		if (registroNuevo.getPorcentajeActasContabilizadas() != null ) {
			registroActual.setPorcentajeActasContabilizadas(registroNuevo.getPorcentajeActasContabilizadas());
		}
		
		if (registroNuevo.getActasObservadasEnviadas() != null ) {
			registroActual.setActasObservadasEnviadas(registroNuevo.getActasObservadasEnviadas());
		}
		
		if (registroNuevo.getPorcentajeActasObservadasEnviadas() != null ) {
			registroActual.setPorcentajeActasObservadasEnviadas(registroNuevo.getPorcentajeActasObservadasEnviadas());
		}
		
		if (registroNuevo.getActasPendientes() != null ) {
			registroActual.setActasPendientes(registroNuevo.getActasPendientes());
		}
		
		if (registroNuevo.getPorcentajeActasPendientes() != null ) {
			registroActual.setPorcentajeActasPendientes(registroNuevo.getPorcentajeActasPendientes());
		}
		
		if (registroNuevo.getDetalle() != null ) {
			registroActual.setDetalle(registroNuevo.getDetalle());
		}
		
	}

}
