package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProcesoElectoralActivoResponse
{
    private Long id;
    private String nombre;
    private String acronimo;
    private Date fechaProceso;
    private Date fechaConvocatoria;
    private int idEleccionPrincipal;
    private Boolean activoFechaProceso;
 }
