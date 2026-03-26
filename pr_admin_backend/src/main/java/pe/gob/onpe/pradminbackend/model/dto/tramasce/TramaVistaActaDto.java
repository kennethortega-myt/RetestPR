package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaActaDto {

    @NotNull(message = "id es obligatorio")
    private Integer idFila;
	private Integer numeroMesa;
	private String  mesa;
	private String  numeroCopia;
	private Integer detalleUbigeoEleccion;
	private Integer idEleccion;
	private Integer ambitoGeografico;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String ubigeoNivel03;
	private String centroPoblado;
	private String codigoLocalVotacion;
	private String nombreLocalVotacion;
	private Long totalElectoresHabiles;
	private Long totalVotosEmitidos;
	private Long totalVotosValidos;
	private Long totalAsistentes;
	private Double  porcentParticipacionCiudadana;
	private String estadoActa;
	private String estadoComputo;
	private String descripcionEstadoActa;

    private List<String> detalle;
	private List<String> lineaTiempo;
}
