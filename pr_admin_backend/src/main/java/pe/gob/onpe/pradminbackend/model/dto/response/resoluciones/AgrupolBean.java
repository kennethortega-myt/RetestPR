package pe.gob.onpe.pradminbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AgrupolBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long idAgrupol;
    private String codiAgrupol;
    private String idDetActa;
    private String nombreAgrupacionPolitica;
    private String votos;
    private Long posicion;
    private String errorMaterial;
    private String ilegible;
    private String activo;

}
