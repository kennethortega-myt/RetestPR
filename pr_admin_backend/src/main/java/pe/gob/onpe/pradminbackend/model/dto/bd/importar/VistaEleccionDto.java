package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VistaEleccionDto {
	@JacksonXmlProperty(localName = "vw_pr_eleccion_distrital")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrEleccionDistrital;
	
	@JacksonXmlProperty(localName = "vw_pr_congresales")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrCongresales;
	
	@JacksonXmlProperty(localName = "vw_pr_diputados")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrDiputados;
	
	@JacksonXmlProperty(localName = "vw_pr_presidente_y_vicepresidentes")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrPresidenciales;
	
	@JacksonXmlProperty(localName = "vw_pr_parlamento_andino")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrParlamentoAndino;
	
	@JacksonXmlProperty(localName = "vw_pr_senadores_distrito_unico")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrSenadoresDistritoNacionalUnico;
	
	@JacksonXmlProperty(localName = "vw_pr_senadores_distrito_multiple")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<VwPrEleccionDto> vwPrSenadoresDistritoElectoralMultiple;
	
	@JacksonXmlProperty(localName = "vw_pr_revocatoria_distrital")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<VwPrEleccionDto> vwPrRevocatoriaDistrital;
}
