package pe.gob.onpe.consultaopcron.model.bd.documents.secondary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tab_cron_reporte_actas")

public class TabReporteActa {

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

    @Field(name = "n_tipo_generacion_reporte")
    private Integer tipoGeneracionReporte; 

    @Field(name = "n_tiempo_consulta_reporte")
    private Integer tiempoConsultaReporte; 

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
    private Integer tipoGeneracionReporteValor; 

    @Field(name = "n_tipo_generacionReporteValorCron")
    private Integer tipoGeneracionReporteValorCron;

}
