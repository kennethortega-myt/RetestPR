package pe.gob.onpe.pradminbackend.model.dto.padron;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PadronDto {

    private String dni;
    private String mesa;
}
