package pe.gob.onpe.pradminbackend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReturnMonitoreoActas {
    private String total;
    private String totalNormales;
    private String totalObservadas;
    private String totalEnviadasJne;
    private List<MonitoreoListActaItem> listActaItems;
}