package pe.gob.onpe.consultaopbackend.model.dto.reporte;

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
    List<ReporteHistorialDto> content;
}
