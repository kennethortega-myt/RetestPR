package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImportarPaginadoDto {
	private long totalRegistros;
    private long totalPaginas;
    private String paginaSiguiente;
    private String paginaAnterior;
    private boolean next;
    private List<MaePadronDto> data;
}
