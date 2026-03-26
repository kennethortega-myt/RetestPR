package pe.gob.onpe.consultaopbackend.model.dto.actas;

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
