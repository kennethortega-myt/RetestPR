package pe.gob.onpe.presentacionbackend.model.dto.actas;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
