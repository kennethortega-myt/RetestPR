package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VwPrTotalCandidatosPorAgrupacionPoliticaExportDto {
	private Long idFila;
	private Integer ambitoGeografico;
    private Integer eleccion;
    private Integer distritoElectoral;
    private Integer ubigeoNivel01;
    private Integer ubigeoNivel02;
    private Integer ubigeo;
    private Integer detUbigeoEleccion;
    private Integer agrupacionPolitica;
    private Integer posicion;
    private String codigo;
    private String descripcion;
    private String totalCandidatos;
}
