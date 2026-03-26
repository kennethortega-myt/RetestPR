package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.ObjetoReporte10porciento;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.ObjetoReporte20porciento;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.ObjetoReporte5porciento;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteAutomaticoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteAutomaticoService;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TabReporteAutomaticoServiceImpl implements TabReporteAutomaticoService {
	
	private final TabReporteAutomaticoRepository tabReporteAutomaticoRepository;

	public TabReporteAutomaticoServiceImpl(
			TabReporteAutomaticoRepository tabReporteAutomaticoRepository) {
		super();
		this.tabReporteAutomaticoRepository = tabReporteAutomaticoRepository;
	}

	@Override
	public void save(TabReporteAutomatico k) {
		this.tabReporteAutomaticoRepository.save(k);
	}

	@Override
	public void saveAll(List<TabReporteAutomatico> k) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TabReporteAutomatico> findAll() {
		return this.tabReporteAutomaticoRepository.findAll();
	}
	
	@Override
	public List<TabReporteAutomaticoResDto> obtenerTodos() {
		List<TabReporteAutomatico> lstTabReporteAutomatico = this.tabReporteAutomaticoRepository.findAll();
		if(!lstTabReporteAutomatico.isEmpty()) {
			return lstTabReporteAutomatico!=null?lstTabReporteAutomatico.stream()
				.map(obj -> {
					TabReporteAutomaticoResDto ra = new TabReporteAutomaticoResDto();
					ra.setId(obj.getId());
					ra.setEleccion(obj.getEleccion());
					ra.setEleccionId(obj.getEleccionId());
					ra.setFechaInicio(obj.getFechaInicio().toString());
					ra.setHoraInicio(obj.getHoraInicio().toString());
					ra.setTipoReporte(obj.getTipoReporte());
					ra.setTipoGeneracionReporte(obj.getTipoGeneracionReporte());
					ra.setTipoGeneracionReporteVal(obj.getTipoGeneracionReporte()==1?obj.getTipoGeneracionReporteValorCron():obj.getTipoGeneracionReporteValor());
					ra.setEstado(obj.getEstado());
					ra.setEstadoDescripcion(obj.getEstado() == 0 ? "Inactivo": "Activo");
					return ra;
				}).toList()
				:Collections.emptyList();
		} else {
			//trow list no registros
			return Collections.emptyList();
		}
	}

	@Override
	public TabReporteAutomaticoResDto obtenerPorId(String id) {
		Optional<TabReporteAutomatico> existingTabReporteAutomatico = this.tabReporteAutomaticoRepository.findById(id);
		if(existingTabReporteAutomatico.isPresent()) {
			TabReporteAutomatico ra = existingTabReporteAutomatico.get();
			TabReporteAutomaticoResDto d = new TabReporteAutomaticoResDto();
			d.setId(ra.getId());
			d.setEleccion(ra.getEleccion());
			d.setEleccionId(ra.getEleccionId());
			d.setFechaInicio(ra.getFechaInicio().toString());
			d.setHoraInicio(ra.getHoraInicio().toString());
			d.setTipoReporte(ra.getTipoReporte());
			d.setTipoGeneracionReporte(ra.getTipoGeneracionReporte());
			d.setTipoGeneracionReporteVal(ra.getTipoGeneracionReporte()==1?ra.getTipoGeneracionReporteValorCron():ra.getTipoGeneracionReporteValor());
			return d;
		} else {
			//trhow
			return null;
		}
		
	}

	@Override
	public TabReporteAutomaticoResDto crear(TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto) {
		String cronExpresion = generarExpresionCron(tabReporteAutomaticoReqDto.getTipoGeneracionReporteVal());
		TabReporteAutomatico ra = new TabReporteAutomatico();
		
		ra.setEleccion(tabReporteAutomaticoReqDto.getEleccion());
		ra.setEleccionId(tabReporteAutomaticoReqDto.getEleccionId());
		ra.setFechaInicio(tabReporteAutomaticoReqDto.getFechaInicio());
		ra.setHoraInicio(tabReporteAutomaticoReqDto.getHoraInicio());
		ra.setTipoReporte(tabReporteAutomaticoReqDto.getTipoReporte());
		ra.setTipoGeneracionReporte(tabReporteAutomaticoReqDto.getTipoGeneracionReporte());
		ra.setEstado(1);
		ra.setEstadoProceso(TipoEstadoProcesoEnum.PENDIENTE.getCodigo().intValue());
        ra.setFechaCreacion(new Date());
        ra.setCAudUsuarioCreacion(tabReporteAutomaticoReqDto.getUsuario());
        ra.setNActivo(1);
		
		if(tabReporteAutomaticoReqDto.getTipoGeneracionReporte()==1) { //tiempo			
			ra.setTipoGeneracionReporteValorCron(tabReporteAutomaticoReqDto.getTipoGeneracionReporteVal());
			ra.setExpresionCron(cronExpresion);
		} else if(tabReporteAutomaticoReqDto.getTipoGeneracionReporte()==2) { //porcentaje			
			ra.setTipoGeneracionReporteValor(tabReporteAutomaticoReqDto.getTipoGeneracionReporteVal());
			ra.setTiempoConsultaReporte(1);
			ra.setReporte5porciento(ObjetoReporte5porciento.builder().build());
			ra.setReporte10porciento(ObjetoReporte10porciento.builder().build());
			ra.setReporte20porciento(ObjetoReporte20porciento.builder().build());
		}
		
		this.tabReporteAutomaticoRepository.save(ra);
		TabReporteAutomaticoResDto d = new TabReporteAutomaticoResDto();
		d.setId(ra.getId());
		d.setEleccion(ra.getEleccion());
		d.setEleccionId(ra.getEleccionId());
		d.setFechaInicio(ra.getFechaInicio().toString());
		d.setHoraInicio(ra.getHoraInicio().toString());
		d.setTipoReporte(ra.getTipoReporte());
		d.setTipoGeneracionReporte(ra.getTipoGeneracionReporte());
		d.setTipoGeneracionReporteVal(ra.getTipoGeneracionReporte()==1?ra.getTipoGeneracionReporteValorCron():ra.getTipoGeneracionReporteValor());		
		return d;
	}

	@Override
	public TabReporteAutomaticoResDto actualizar(String id, TabReporteAutomaticoReqDto req) {

		return this.tabReporteAutomaticoRepository.findById(id)
			.map(ra -> {

				// Solo actualiza si viene no nulo
				if (req.getFechaInicio() != null) {
					ra.setFechaInicio(req.getFechaInicio());
				}

				if (req.getHoraInicio() != null) {
					ra.setHoraInicio(req.getHoraInicio());
				}

				if (req.getTipoReporte() != null) {
					ra.setTipoReporte(req.getTipoReporte());
				}

				if (req.getEstado() != null) {
					ra.setEstado(req.getEstado());
				}

				if (req.getTipoGeneracionReporte() != null) {
					ra.setTipoGeneracionReporte(req.getTipoGeneracionReporte());

					// ⚙️ Lógica condicional según tipo de generación
					if (req.getTipoGeneracionReporte() == 1) { // tiempo
						if (req.getTipoGeneracionReporteVal() != null) {
							String cronExpresion = generarExpresionCron(req.getTipoGeneracionReporteVal());
							ra.setTipoGeneracionReporteValorCron(req.getTipoGeneracionReporteVal());
							ra.setExpresionCron(cronExpresion);
						}
					} else if (req.getTipoGeneracionReporte() == 2) { // porcentaje
						if (req.getTipoGeneracionReporteVal() != null) {
							ra.setTipoGeneracionReporteValor(req.getTipoGeneracionReporteVal());
						}
						if (ra.getTiempoConsultaReporte() == null) {
							ra.setTiempoConsultaReporte(1);
						}
						if (ra.getReporte5porciento() == null) {
							ra.setReporte5porciento(ObjetoReporte5porciento.builder().build());
						}
						if (ra.getReporte10porciento() == null) {
							ra.setReporte10porciento(ObjetoReporte10porciento.builder().build());
						}
						if (ra.getReporte20porciento() == null) {
							ra.setReporte20porciento(ObjetoReporte20porciento.builder().build());
						}
					}
				}

                ra.setCAudUsuarioModificacion(req.getUsuario());
                ra.setDAudFechaModificacion(new Date());
				this.tabReporteAutomaticoRepository.save(ra);

				// Construcción del DTO de respuesta
				TabReporteAutomaticoResDto res = new TabReporteAutomaticoResDto();
				res.setId(ra.getId());
				res.setEleccion(ra.getEleccion());
				res.setEleccionId(ra.getEleccionId());
				res.setFechaInicio(ra.getFechaInicio() != null ? ra.getFechaInicio().toString() : null);
				res.setHoraInicio(ra.getHoraInicio() != null ? ra.getHoraInicio().toString() : null);
				res.setEstado(ra.getEstado());
				res.setTipoReporte(ra.getTipoReporte());
				res.setTipoGeneracionReporte(ra.getTipoGeneracionReporte());
				res.setTipoGeneracionReporteVal(
					ra.getTipoGeneracionReporte() != null && ra.getTipoGeneracionReporte() == 1
						? ra.getTipoGeneracionReporteValorCron()
						: ra.getTipoGeneracionReporteValor()
				);

				return res;
			})
			.orElse(null);
	}


	@Override
	public void Eliminar(String id) {
		// TODO Auto-generated method stub
		
	}

	private String generarExpresionCron(Integer tiempo) {
        if(tiempo<60) { //para config de cada 30 min
        	return String.format("0 0/%d * * * *", tiempo);
        } else {
        	tiempo = tiempo/60; //para config de cada hora
        	return String.format("0 0 0/%d * * *", tiempo);
        }
    }
}
