package pe.gob.onpe.presentacionbackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IconosTipoEleccionResponse {
    private String nombreTipoEleccion;
    private String codigoTipoEleccion;
    private String iconoTipoEleccion;

    @Override
    public String toString() {
        return "nombreTipoEleccion: " + this.nombreTipoEleccion + "\ncodigoTipoEleccion: " + this.codigoTipoEleccion + "\niconoTipoEleccion: " + this.iconoTipoEleccion;
    }
}
