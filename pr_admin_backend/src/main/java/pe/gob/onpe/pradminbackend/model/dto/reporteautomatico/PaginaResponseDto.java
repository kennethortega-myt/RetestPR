package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginaResponseDto<T> {
	private Integer page;
	private long size;
	private long rows;
	private List<T> content;
}
