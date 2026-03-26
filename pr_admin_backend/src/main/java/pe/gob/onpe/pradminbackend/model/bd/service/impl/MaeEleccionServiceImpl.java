package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeEleccionRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeEleccionRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.repository.TabReporteCandidatoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeEleccionService;

import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.dto.maeeleccion.MaeEleccionSelectResDto;
import pe.gob.onpe.pradminbackend.model.dto.response.EleccionesMenuResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaeEleccionServiceImpl implements MaeEleccionService {


    private final MaeEleccionRepositoryCustom maeEleccionRepositoryCustom;
    private final TabReporteCandidatoRepository tabReporteCandidatoRepository;

    @Override
    public List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo) {
        log.info("Ingresando a findEleccionesByProceso con idProceso: {} y activo: {}", idProceso, activo);
        return this.maeEleccionRepositoryCustom.findEleccionesByProceso(idProceso, activo);
    }

    @Override
    public List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProcesoForConfigReport(Long idProceso) {
        log.info("Ingressa a obtenerMaeEleccionSelectByProcesoForConfigReport con idProceso: {}", idProceso);
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
    public List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProceso(Long idProceso) {
        log.info("Ingresando a obtenerMaeEleccionSelectByProceso con idProceso: {}", idProceso);
        List<MaeEleccion> lstMaeEleccion = this.maeEleccionRepositoryCustom.findMaeEleccionByProceso(idProceso);
        return lstMaeEleccion.stream()
            .map(eleccion -> {
                MaeEleccionSelectResDto mesr = new MaeEleccionSelectResDto();
                mesr.setCodigo(eleccion.getCodigo());
                mesr.setNombre(formatearNombre(eleccion));

                Integer idEleccion = Integer.valueOf(eleccion.getCodigo());

                tabReporteCandidatoRepository.findById(idEleccion)
                    .ifPresent(reporte -> {
                        mesr.setExito(reporte.getExito());
                    });

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



    private final MaeEleccionRepository maeEleccionRepository;

    @Override
    public void save(MaeEleccion k) {
        this.maeEleccionRepository.save(k);
    }

    @Override
    public void saveAll(List<MaeEleccion> k) {
        this.maeEleccionRepository.saveAll(k);
    }

    public List<MaeEleccion> findAll() {
        return this.maeEleccionRepository.findAll();
    }

	@Override
    public void deleteAll() {
        this.maeEleccionRepository.deleteAll();
    }

}
