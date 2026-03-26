package pe.gob.onpe.pradminbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AplicarActaBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String idResolucion;
    private String mesa;
    private String copia;
    private boolean siguiente;
}
