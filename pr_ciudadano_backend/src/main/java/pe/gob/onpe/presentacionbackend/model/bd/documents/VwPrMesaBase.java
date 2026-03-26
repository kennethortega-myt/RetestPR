package pe.gob.onpe.presentacionbackend.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrMesaBase extends Auditoria {
	
	@Field(name = "c_tipo_filtro")
	private String tipoFiltro;
	
	@Field(name = "n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Field(name = "n_distrito_electoral")
	private Integer distritoElectoral;
	
	@Field(name = "n_ubigeo_nivel_01")
	private Integer ubigeoNivel01;
	
	@Field(name = "n_ubigeo_nivel_02")
	private Integer ubigeoNivel02;
	
	@Field(name = "n_ubigeo_nivel_03")
	private Integer ubigeoNivel03;
	
	@Field(name = "n_total_mesas")
	private Integer totalMesas;
	
	@Field(name = "n_mesas_instaladas")
	private Integer mesasInstaladas;
	
	@Field(name = "n_mesas_no_instaladas")
	private Integer mesasNoInstaladas;
	
	@Field(name = "n_mesas_por_informar")
	private Integer mesasPorInformar;
	
}
