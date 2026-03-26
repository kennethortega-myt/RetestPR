package pe.gob.onpe.pradminbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ActaBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long actaId;
    private Long mesaId;
    private String resolucionId;
    private String mesa;
    private String copia;
    private String eleccion;
    private String estadoActa;
    private String estadoMesa;
    private String estadoDigitacion;
    private String descripcionEstadoActa;
    private String descripcionEstadoMesa;
    private Long electoresHabiles;
    private String cvas;
    private String ubigeo;
    private String localVotacion;
    private String fecha;
    private String imagenEscrutinio;
    private String horaEscrutinio;
    private String imagenInstalacion;
    private String horaInstalacion;
    private String errorMaterial;
    private String tipoErrorM;
    private String votosImpugnados;
    private String ilegibilidad;
    private String tipoIlegible;
    private String detalleIlegible;
    private String actasIncompletas;
    private String solNulidad;
    private String actaSinDatos;
    private String actaSinFirma;
    private String observacion;
    private String observacionesJNE;
    private String tipoLote;
    private String extraviada;
    private String obsMesa;
    private List<AgrupolBean> agrupacionesPoliticas;

}
