package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

@Data
public class EleccionDistritalResumenResponse
{
    private Double totalActas;
    private Double participacionCiudadana;
    private Double actasContabilizadas;
    private Double actasEnviadasJee;
    private Double actasPendientes;

}
