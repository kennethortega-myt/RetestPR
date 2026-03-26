package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TotalesDistritalesDto {

    private int total;
    private int totalAlcaldes;
    private int totalRegidores;

}
