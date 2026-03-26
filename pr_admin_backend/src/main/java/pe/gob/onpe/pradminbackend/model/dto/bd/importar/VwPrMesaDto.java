package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VwPrMesaDto {
	private Long id;
	private String tipoFiltro;
	private Integer ambitoGeografico;
	private Integer idDistritoElectoral;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer totalMesas;
	private Integer mesasInstaladas;
	private Integer mesasNoInstaladas;
	private Integer mesasPorInformar;
}
