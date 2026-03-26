package pe.gob.onpe.presentacionbackend.model.dto.reporte;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHistorialPaginado {

    private Integer paginaActual;
    private long  totalRegistros;
    private Integer totalPaginas;
    private String nombreTipoEleccion;
    private String iconoTipoEleccion;
    List<ReporteHistorialDto> content;
}
