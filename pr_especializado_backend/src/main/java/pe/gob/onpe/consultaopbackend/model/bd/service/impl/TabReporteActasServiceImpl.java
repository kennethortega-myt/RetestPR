package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.*;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteActasRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteActasService;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasResDto;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

import java.util.*;

@Service
@Slf4j
public class TabReporteActasServiceImpl implements TabReporteActasService {

	private final TabReporteActasRepository tabReporteActasRepository;

	public TabReporteActasServiceImpl(TabReporteActasRepository tabReporteActasRepository) {
		super();
		this.tabReporteActasRepository = tabReporteActasRepository;
	}

	@Override
	public void save(TabCronReporteActas k) {
		this.tabReporteActasRepository.save(k);
	}

	@Override
	public void saveAll(List<TabCronReporteActas> k) {
        this.tabReporteActasRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
        this.tabReporteActasRepository.deleteAll();
	}

	@Override
	public List<TabCronReporteActas> findAll() {
		return this.tabReporteActasRepository.findAll();
	}

    @Override
    public List<TabReporteActasResDto> obtenerTodos() {
        List<TabCronReporteActas> lstTabReporteActas = this.tabReporteActasRepository.findAll();
        if(!lstTabReporteActas.isEmpty()) {
            return lstTabReporteActas.stream()
                    .map(obj -> {
                        TabReporteActasResDto ra = new TabReporteActasResDto();
                        ra.setId(obj.getId());
                        ra.setEleccion(obj.getEleccion());
                        ra.setEleccionId(obj.getEleccionId());
                        ra.setFechaInicio(obj.getFechaInicio().toString());
                        ra.setHoraInicio(obj.getHoraInicio().toString());
                        ra.setTipoReporte(obj.getTipoReporte());
                        ra.setTipoGeneracionReporte(obj.getTipoGeneracionReporte());
                        ra.setTipoGeneracionReporteVal(getTipoGeneracionReporteVal(obj));
                        ra.setEstado(obj.getEstado());
                        ra.setEstadoDescripcion(getEstadoDescripcion(obj.getEstado()));
                        return ra;
                    }).toList();
        } else {
            return Collections.emptyList();
        }
    }

	@Override
	public TabReporteActasResDto obtenerPorId(String id) {
		Optional<TabCronReporteActas> existingTabReporteAutomatico = this.tabReporteActasRepository.findById(id);
		if(existingTabReporteAutomatico.isPresent()) {
            TabCronReporteActas ra = existingTabReporteAutomatico.get();
			TabReporteActasResDto d = new TabReporteActasResDto();
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
			return null;
		}

	}

	@Override
	public TabReporteActasResDto crear(TabReporteActasReqDto tabReporteActasReqDto) {
		String cronExpresion = generarExpresionCron(tabReporteActasReqDto.getTipoGeneracionReporteVal());
		TabCronReporteActas ra = new TabCronReporteActas();

		ra.setEleccion(tabReporteActasReqDto.getEleccion());
		ra.setEleccionId(tabReporteActasReqDto.getEleccionId());
		ra.setFechaInicio(tabReporteActasReqDto.getFechaInicio());
		ra.setHoraInicio(tabReporteActasReqDto.getHoraInicio());
		ra.setTipoReporte(tabReporteActasReqDto.getTipoReporte());
		ra.setTipoGeneracionReporte(tabReporteActasReqDto.getTipoGeneracionReporte());
		ra.setEstado(1);
		ra.setEstadoProceso(TipoEstadoProcesoEnum.PENDIENTE.getCodigo().intValue());
        ra.setFechaCreacion(new Date());
        ra.setCAudUsuarioCreacion(tabReporteActasReqDto.getUsuario());
        ra.setNActivo(1);

		if(tabReporteActasReqDto.getTipoGeneracionReporte()==1) { //tiempo
			ra.setTipoGeneracionReporteValorCron(tabReporteActasReqDto.getTipoGeneracionReporteVal());
			ra.setExpresionCron(cronExpresion);
		} else if(tabReporteActasReqDto.getTipoGeneracionReporte()==2) { //porcentaje
			ra.setTipoGeneracionReporteValor(tabReporteActasReqDto.getTipoGeneracionReporteVal());
			ra.setTiempoConsultaReporte(1);
			ra.setReporte5porciento(ObjetoReporte5porciento.builder().build());
			ra.setReporte10porciento(ObjetoReporte10porciento.builder().build());
			ra.setReporte20porciento(ObjetoReporte20porciento.builder().build());
		}

		this.tabReporteActasRepository.save(ra);
		TabReporteActasResDto d = new TabReporteActasResDto();
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
	public TabReporteActasResDto actualizar(TabReporteActasReqDto tabReporteActasReqDto) {

		Optional<TabCronReporteActas> existingTabReporteAutomatico = this.tabReporteActasRepository
                .findById(tabReporteActasReqDto.getId());
		if (existingTabReporteAutomatico.isPresent()) {
			TabCronReporteActas ra = existingTabReporteAutomatico.get();

            if (Objects.nonNull(tabReporteActasReqDto.getEstado())) {
                ra.setEstado(tabReporteActasReqDto.getEstado());
            } else {
                String cronExpresion = generarExpresionCron(tabReporteActasReqDto.getTipoGeneracionReporteVal());
                ra.setFechaInicio(tabReporteActasReqDto.getFechaInicio());
                ra.setHoraInicio(tabReporteActasReqDto.getHoraInicio());
                ra.setTipoGeneracionReporte(tabReporteActasReqDto.getTipoReporte());

                if(tabReporteActasReqDto.getTipoGeneracionReporte()==1) { //tiempo
                    ra.setTipoGeneracionReporteValorCron(tabReporteActasReqDto.getTipoGeneracionReporteVal());
                    ra.setExpresionCron(cronExpresion);
                } else if(tabReporteActasReqDto.getTipoGeneracionReporte()==2) { //porcentaje
                    ra.setTipoGeneracionReporteValor(tabReporteActasReqDto.getTipoGeneracionReporteVal());
                    ra.setTiempoConsultaReporte(ObjectUtils.getIfNull(ra.getTiempoConsultaReporte(), 1));
                    ra.setReporte5porciento(ObjectUtils.getIfNull(ra.getReporte5porciento(), ObjetoReporte5porciento.builder().build()));
                    ra.setReporte10porciento(ObjectUtils.getIfNull(ra.getReporte10porciento(), ObjetoReporte10porciento.builder().build()));
                    ra.setReporte20porciento(ObjectUtils.getIfNull(ra.getReporte20porciento(), ObjetoReporte20porciento.builder().build()));
                }
            }

            ra.setCAudUsuarioModificacion(tabReporteActasReqDto.getUsuario());
            ra.setDAudFechaModificacion(new Date());
			this.tabReporteActasRepository.save(ra);

            TabReporteActasResDto actasupdate = new TabReporteActasResDto();
            actasupdate.setId(ra.getId());
            actasupdate.setEstado(ra.getEstado());
            actasupdate.setEleccion(ra.getEleccion());
            actasupdate.setEleccionId(ra.getEleccionId());
            actasupdate.setFechaInicio(ra.getFechaInicio().toString());
            actasupdate.setHoraInicio(ra.getHoraInicio().toString());
            actasupdate.setTipoReporte(ra.getTipoReporte());
            actasupdate.setTipoGeneracionReporte(ra.getTipoGeneracionReporte());
            actasupdate.setTipoGeneracionReporteVal(ra.getTipoGeneracionReporte()==1?ra.getTipoGeneracionReporteValorCron():ra.getTipoGeneracionReporteValor());
			return actasupdate;
		} else {
			return null;
		}
	}

	@Override
	public void Eliminar(String id) {
        this.tabReporteActasRepository.deleteById(id);
	}

	private String generarExpresionCron(Integer tiempo) {
        if(tiempo<60) { //para config de cada 30 min
        	return String.format("0 0/%d * * * *", tiempo);
        } else {
        	tiempo = tiempo/60; //para config de cada hora
        	return String.format("0 0 0/%d * * *", tiempo);
        }
    }

    private String getEstadoDescripcion(int estado) {
        String estadoDescripcion = "Activo";
        if (estado == ConstantesComunes.ESTADO_INACTIVO) {
            estadoDescripcion = "Inactivo";
        }
        return estadoDescripcion;
    }

    private Integer getTipoGeneracionReporteVal(TabCronReporteActas obj) {
        Integer tipoGeneracionReporteVal = obj.getTipoGeneracionReporteValor();
        if (obj.getTipoGeneracionReporte() == 1) {
            tipoGeneracionReporteVal = obj.getTipoGeneracionReporteValorCron();
        }
        return tipoGeneracionReporteVal;
    }

}
