package pe.gob.onpe.consultaopbackend.model.bd.documents.secondary;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "tab_log")
public class TabLog {
	@Id
	private Long id;
	
	@Field(name = "c_objeto")
	private String cObjeto;
	
	@Field(name = "d_fecha_error")
	private Date dFechaError;
	
	@Field(name = "c_sql_error")
	private String cSqlError;
	
	@Field(name = "c_sql_text")
	private String cSqlText;
	
	@Field(name = "c_sql_context")
	private String cSqlContext;
	
	@Field(name = "c_linea")
	private String cLinea;
	
	@Field(name = "c_usuario")
	private String cUsuario;
	
	@Field(name = "c_mensaje")
	private String cMensaje;
}
