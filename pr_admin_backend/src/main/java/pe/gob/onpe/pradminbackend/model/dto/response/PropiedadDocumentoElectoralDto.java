package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class PropiedadDocumentoElectoralDto {


	private Long    id;
	private String  nombreDoc;
	private String  descCorta;
	private Integer  activo;
    private Integer tipoImagen;
    private Integer escanerAmbasCaras;
    private Integer tamanioHoja;
    private Integer multipagina;
    private Integer  codBarOrientacion;
    private Integer  codBarLeft;
    private Integer  codBarTop;
    private Integer  codBarWidth;
    private Integer  codBarHeight;
	
}

