package pe.gob.onpe.presentacionbackend.model.dto.mesa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MesaTotalesDto {


    private Integer mesasInstaladas;

    private Integer mesasNoInstaladas;

    private Integer mesasPendientes;



}
