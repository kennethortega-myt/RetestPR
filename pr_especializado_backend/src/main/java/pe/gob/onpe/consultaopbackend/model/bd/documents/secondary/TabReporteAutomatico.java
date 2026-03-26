package pe.gob.onpe.consultaopbackend.model.bd.documents.secondary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tab_reporte_automatico")
public class TabReporteAutomatico {

	@Id
	private String id;

	@Field(name = "n_eleccion_id")
	private Integer eleccionId;

	@Field(name = "c_eleccion")
	private String eleccion;
	
	@Field(name = "d_fecha_inicio")
    private LocalDate fechaInicio;
    
    @Field(name = "d_hora_inicio")
	private LocalTime horaInicio;
    
    @Field(name = "n_tipo_reporte")
	private Integer tipoReporte;
    
    @Field(name = "c_expresion_cron")
	private String expresionCron;

    @Field("n_porcentaje")
    private Double porcentaje;

	@Field(name = "n_tipo_generacion_reporte")
	private Integer tipoGeneracionReporte; //1:por tiempo, 2: por porcentaje

	@Field(name = "n_tiempo_consulta_reporte")
	private Integer tiempoConsultaReporte; // cada tantos minutos se debe consultar si cumple para generar reporte para (2)

	@Field(name = "n_estado")
	private Integer estado;

	@Field(name = "n_estado_proceso")
	private Integer estadoProceso;

	@Field(name = "o_reporte_5_porciento")
	private ObjetoReporte5porciento reporte5porciento;

	@Field(name = "o_reporte_10_porciento")
	private ObjetoReporte10porciento reporte10porciento;

	@Field(name = "o_reporte_20_porciento")
	private ObjetoReporte20porciento reporte20porciento;

	@Field(name = "n_tipo_generacionReporteValor")
	private Integer tipoGeneracionReporteValor; //5,10 o 20 porciento
	
	@Field(name = "n_tipo_generacionReporteValorCron")
	private Integer tipoGeneracionReporteValorCron; //30, 1 hora o 2 horas tiempo

    @Field(name = "n_activo")
    private Integer nActivo;

    @Field(name = "c_aud_usuario_creacion")
    private String cAudUsuarioCreacion;

    @Field(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Field(name = "c_aud_usuario_modificacion")
    private String cAudUsuarioModificacion;

    @Field(name = "d_aud_fecha_modificacion")
    private Date dAudFechaModificacion;

}
