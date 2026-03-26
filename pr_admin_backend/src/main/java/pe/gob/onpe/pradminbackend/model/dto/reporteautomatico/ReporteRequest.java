package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {

    private Integer idEleccion;
    private Integer idAmbitoGeografico;

    private Integer idDistritoElectoral;

    private String ubigeoNivel01;
    private String descripcionUbigeoNivel1;

    private String ubigeoNivel02;
    private String descripcionUbigeoNivel2;

    private String idUbigeo;
    private String descripcionUbigeoNivel3;

    private Long codigoLocalVotacion;
    private String descripcionLocalVotacion;

    private String codigoOp;
    private String codigoUsuario;
    private String descripcionUsuario;
    private Integer tipoReporte;

    private String tipoFiltro;

    @Override
    public String toString() {
        return "ReporteRequest{" +
                "idEleccion=" + idEleccion +
                ", idAmbitoGeografico=" + idAmbitoGeografico +
                ", ubigeoNivel01='" + ubigeoNivel01 + '\'' +
                ", descripcionUbigeoNivel1='" + descripcionUbigeoNivel1 + '\'' +
                ", ubigeoNivel02='" + ubigeoNivel02 + '\'' +
                ", descripcionUbigeoNivel2='" + descripcionUbigeoNivel2 + '\'' +
                ", idUbigeo='" + idUbigeo + '\'' +
                ", descripcionUbigeoNivel3='" + descripcionUbigeoNivel3 + '\'' +
                ", codigoLocalVotacion=" + codigoLocalVotacion +
                ", descripcionLocalVotacion='" + descripcionLocalVotacion + '\'' +
                ", codigoOp='" + codigoOp + '\'' +
                ", codigoUsuario='" + codigoUsuario + '\'' +
                ", descripcionUsuario='" + descripcionUsuario + '\'' +
                ", tipoReporte=" + tipoReporte +
                ", tipoFiltro='" + tipoFiltro + '\'' +
                '}';
    }
}
