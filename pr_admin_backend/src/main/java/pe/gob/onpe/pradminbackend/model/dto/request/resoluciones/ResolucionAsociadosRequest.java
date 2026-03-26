package pe.gob.onpe.pradminbackend.model.dto.request.resoluciones;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pe.gob.onpe.pradminbackend.model.dto.response.resoluciones.ActaBean;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ResolucionAsociadosRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
    String idArchivo;
    String nombreArchivo;
    Integer procedencia;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate fechaResolucion;//para registro
    Date fechaResolucion2;//para mostrar, edicion
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    Date fechaRegistro;
    String numeroExpediente;
    String numeroResolucion;
    Integer tipoResolucion;
    String estadoResolucion;
    String descripcionEstadoResolucion;
    Integer numeroPaginas;
    String estadoDigitalizacion;
    String descripcionEstadoDigitalizacion;
    private List<ActaBean> actasAsociadas = new ArrayList<>();

}
