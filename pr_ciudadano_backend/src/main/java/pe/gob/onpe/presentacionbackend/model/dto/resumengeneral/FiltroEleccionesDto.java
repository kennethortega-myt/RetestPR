package pe.gob.onpe.presentacionbackend.model.dto.resumengeneral;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroEleccionesDto {
    private Long idProceso;
    private Integer activo;
    private Integer ubigeoNivel03;

    private Integer idAmbitoGeografico;
    private String tipoFiltro;
    private Integer ubigeoNivel01;
    private Integer ubigeoNivel02;
}
