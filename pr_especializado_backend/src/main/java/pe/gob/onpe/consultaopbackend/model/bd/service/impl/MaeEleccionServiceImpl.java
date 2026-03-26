package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeEleccionRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeEleccionRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeEleccionService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteActasService;
import pe.gob.onpe.consultaopbackend.model.dto.maeeleccion.MaeEleccionSelectResDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasResDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.EleccionesMenuResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaeEleccionServiceImpl implements MaeEleccionService {

    @Autowired
    private MaeEleccionRepository maeEleccionRepository;

    @Autowired
    private MaeEleccionRepositoryCustom maeEleccionRepositoryCustom;

    @Autowired
    private TabReporteActasService tabReporteActasService;

    @Override
    public void save(MaeEleccion k) {
        this.maeEleccionRepository.save(k);
    }

    @Override
    public void saveAll(List<MaeEleccion> k) {
        this.maeEleccionRepository.saveAll(k);
    }

    @Override
    public void delete(Long idCentroComputo, String proceso) {
        this.maeEleccionRepositoryCustom.deleteByIdCentroComputoAndProceso(idCentroComputo, proceso);
    }

    public List<MaeEleccion> findAll() {
        return this.maeEleccionRepository.findAll();
    }

    @Override
    public List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo) {

        return this.maeEleccionRepositoryCustom.findEleccionesByProceso(idProceso, activo);

    }

    @Override
    public void deleteAll() {
        this.maeEleccionRepository.deleteAll();
    }

    @Override
    public Optional<MaeEleccion> findById(Long id) {
        return this.maeEleccionRepository.findById(id);
    }

    @Override
    public List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProceso(Long idProceso) {
        List<MaeEleccion> lstMaeEleccion = this.maeEleccionRepositoryCustom.findMaeEleccionByProceso(idProceso);
        return lstMaeEleccion.stream()
                .map(eleccion -> {
                    MaeEleccionSelectResDto mesr = new MaeEleccionSelectResDto();
                    mesr.setCodigo(eleccion.getCodigo());

                    String nombreFormateado = formatearNombre(eleccion);
                    mesr.setNombre(nombreFormateado);

                    return mesr;
                }).toList();
    }
    
    @Override
    public List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProcesoForConfigReport(Long idProceso) {
        List<MaeEleccion> lstMaeEleccion = this.maeEleccionRepositoryCustom.findMaeEleccionByProcesoForConfigReport(idProceso);
        return lstMaeEleccion.stream()
                .map(eleccion -> {
                    MaeEleccionSelectResDto mesr = new MaeEleccionSelectResDto();
                    mesr.setCodigo(eleccion.getCodigo());

                    String nombreFormateado = formatearNombre(eleccion);
                    mesr.setNombre(nombreFormateado);

                    return mesr;
                }).toList();
    }

    @Override
    public List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByReporteActas(Long idProceso) {

        List<TabReporteActasResDto> lsTabReporteActasResDtos = tabReporteActasService.obtenerTodos();

        Map<Integer,String> reporteActasMap = lsTabReporteActasResDtos.stream()
                .collect(Collectors.toMap(
                        TabReporteActasResDto::getEleccionId,
                        TabReporteActasResDto::getEleccion));


        List<MaeEleccion> lstMaeEleccione = this.maeEleccionRepositoryCustom.findMaeEleccionByProceso(idProceso);
        return lstMaeEleccione.stream()
                .filter(c -> !reporteActasMap.containsKey(c.getId().intValue()))
                .map(eleccion -> {
                    MaeEleccionSelectResDto mesr = new MaeEleccionSelectResDto();
                    mesr.setCodigo(eleccion.getCodigo());

                    String nombreFormateado = formatearNombre(eleccion);
                    mesr.setNombre(nombreFormateado);

                    return mesr;
                }).toList();
    }

    private String formatearNombre(MaeEleccion eleccion) {

        return switch (eleccion.getCodigo()) {
            case "10" -> "PRESIDENCIAL";
            case "12" -> "Parlamento Andino";
            case "13" -> "Diputados";
            case "14" -> "Senadores distrito electoral múltiple";
            case "15" -> "Senadores distrito electoral único";
            default -> capitalizarPrimeraLetra(eleccion.getNombre());
        };
    }

    private String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() +
                texto.substring(1).toLowerCase();
    }

}
