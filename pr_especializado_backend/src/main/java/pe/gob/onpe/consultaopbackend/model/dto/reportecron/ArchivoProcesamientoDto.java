package pe.gob.onpe.consultaopbackend.model.dto.reportecron;

import lombok.*;

import java.io.File;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoProcesamientoDto {
    private File archivoOriginal;
    private String cGuid;
    private String extension;
    private Long nIdActa;
    private Integer nTipo;
    private Long nEleccion;
    private String cCodigoMesa;
    private String cUbigeoNivel01;
    private Integer nroOrden;
    private String nuevoNombre;
}