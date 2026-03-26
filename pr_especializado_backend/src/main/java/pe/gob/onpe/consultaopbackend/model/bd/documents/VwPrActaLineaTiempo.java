package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

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
