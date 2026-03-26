package pe.gob.onpe.presentacionbackend.model.dto.response;

import org.springframework.data.mongodb.core.mapping.Field;

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
public class TiposEleccionResponse {
    @Field("c_nombre")
    private String nombreTipoEleccion;
    @Field("c_codigo")
    private String codigoTipoEleccion;

    @Override
    public String toString() {
        return  "nombreTipoEleccion = '" + this.nombreTipoEleccion + '\'' +
                ", codigoTipoEleccion = '" + this.codigoTipoEleccion + '\'';
    }
}
