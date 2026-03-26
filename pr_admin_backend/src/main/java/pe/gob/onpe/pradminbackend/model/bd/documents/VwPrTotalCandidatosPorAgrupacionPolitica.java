package pe.gob.onpe.pradminbackend.model.bd.documents;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vw_total_candidatos_por_agrupacion_politica")
public class VwPrTotalCandidatosPorAgrupacionPolitica {
	@Id
	@Field("id")
	private Long id;
	
	@Field(name = "n_eleccion")
	private Integer eleccion;
	
	@Field(name = "n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Field(name = "n_distrito_electoral")
    private Integer distritoElectoral;
	
	@Field(name = "n_ubigeo_nivel_01")
    private Integer ubigeoNivel01;
	
	@Field(name = "n_ubigeo_nivel_02")
    private Integer ubigeoNivel02;
	
	@Field(name = "n_ubigeo")
    private Integer ubigeo;
	
	@Field(name = "n_det_ubigeo_eleccion")
    private Integer detUbigeoEleccion;
	
	@Field(name = "n_agrupacion_politica")
    private Integer agrupacionPolitica;
	
	@Field(name = "n_posicion")
    private Integer posicion;
	
	@Field(name = "c_codigo")
    private String codigo;
	
	@Field(name = "c_descripcion")
    private String descripcion;
	
	@Field(name = "n_total_candidatos")
    private String totalCandidatos;
}
