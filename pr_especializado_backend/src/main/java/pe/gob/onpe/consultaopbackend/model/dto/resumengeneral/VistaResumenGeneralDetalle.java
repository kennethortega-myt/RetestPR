package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaResumenGeneralDetalle {


    private Integer idAgrupacionPolitica;
    private String codigo;
    private String descripcion;
    private Integer totalVotos;
    private Double porcentajeVotosValidos;
    private Double porcentajeVotosEmitidos;
    private Integer grafico;

    private List<VistaResumenGeneralCandidato> candidatos;

}
