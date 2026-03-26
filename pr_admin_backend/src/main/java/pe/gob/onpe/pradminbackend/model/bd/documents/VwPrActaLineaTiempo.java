package pe.gob.onpe.pradminbackend.model.bd.documents;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrActaLineaTiempo {
	
	@Field("c_codigo_estado_acta")
	private String codigoEstadoActa;
	
	@Field("c_descripcion_estado_acta")
	private String descripcionEstadoActa;
	
	@Field("c_descripcion_estado_acta_resolucion")
	private String descripcionEstadoActaResolucion;
	
	@Field("d_fecha_registro")
	private Date fechaRegistro;
}
