package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.ObjetoReporte10porciento;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.ObjetoReporte20porciento;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.ObjetoReporte5porciento;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteAutomaticoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.TabReporteAutomaticoService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.TabReporteAutomaticoReqDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;
import pe.gob.onpe.pradminbackend.utils.DateUtil;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TabReporteAutomaticoServiceImpl implements TabReporteAutomaticoService {
	
	private final TabReporteAutomaticoRepository tabReporteAutomaticoRepository;

	public TabReporteAutomaticoServiceImpl(TabReporteAutomaticoRepository tabReporteAutomaticoRepository) {
		super();
		this.tabReporteAutomaticoRepository = tabReporteAutomaticoRepository;
	}

	@Override
	public void save(TabReporteAutomatico k) {
		this.tabReporteAutomaticoRepository.save(k);
	}

    @Override
    public void saveAll(List<TabReporteAutomatico> k) {
        this.tabReporteAutomaticoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.tabReporteAutomaticoRepository.deleteAll();
    }

    @Override
	public List<TabReporteAutomatico> findAll() {
		return this.tabReporteAutomaticoRepository.findAll();
	}
	
	@Override
	public List<TabReporteAutomaticoResDto> obtenerTodos() {
		List<TabReporteAutomatico> lstTabReporteAutomatico = this.tabReporteAutomaticoRepository.findAll();
		if(!lstTabReporteAutomatico.isEmpty()) {
			return lstTabReporteAutomatico.stream()
				.map(obj -> {
					TabReporteAutomaticoResDto ra = new TabReporteAutomaticoResDto();
					ra.setId(obj.getId());
					ra.setEleccion(obj.getEleccion());
					ra.setEleccionId(obj.getEleccionId());
					ra.setFechaInicio(obj.getFechaInicio().toString());
					ra.setHoraInicio(obj.getHoraInicio().toString());
					ra.setTipoReporte(obj.getTipoReporte());
					ra.setTipoGeneracionReporte(obj.getTipoGeneracionReporte());
					if(obj.getTipoGeneracionReporte() == 1){
						ra.setTipoGeneracionReporteVal(obj.getTipoGeneracionReporteValorCron());
					}else{
						ra.setTipoGeneracionReporteVal(obj.getTipoGeneracionReporteValor());
					}
					ra.setEstado(obj.getEstado());
					if(obj.getEstado() == 0){
						ra.setEstadoDescripcion("Inactivo");
					}else{
						ra.setEstadoDescripcion("Activo");
					}
					return ra;
				}).toList();
		} else {
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
		ra.setFechaInicio(DateUtil.stringToLocalDate(tabReporteAutomaticoReqDto.getFechaInicio(), "yyyy-MM-dd"));
		ra.setHoraInicio(DateUtil.stringToLocalTime(tabReporteAutomaticoReqDto.getHoraInicio(), "HH:mm"));
		ra.setTipoReporte(tabReporteAutomaticoReqDto.getTipoReporte());
		ra.setTipoGeneracionReporte(tabReporteAutomaticoReqDto.getTipoGeneracionReporte());
		ra.setEstado(1);
        ra.setFechaCreacion(new Date());
        ra.setCAudUsuarioCreacion(tabReporteAutomaticoReqDto.getUsuario());
		ra.setEstadoProceso(TipoEstadoProcesoEnum.PENDIENTE.getCodigo().intValue());
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
					ra.setFechaInicio(DateUtil.stringToLocalDate(req.getFechaInicio(), "yyyy-MM-dd"));
				}

				if (req.getHoraInicio() != null) {
					ra.setHoraInicio(DateUtil.stringToLocalTime(req.getHoraInicio(), "HH:mm"));
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

	private String generarExpresionCron(Integer tiempo) {
        if(tiempo<60) { //para config de cada 30 min
        	return String.format("0 0/%d * * * *", tiempo);
        } else {
        	tiempo = tiempo/60; //para config de cada hora
        	return String.format("0 0 0/%d * * *", tiempo);
        }
    }
}
