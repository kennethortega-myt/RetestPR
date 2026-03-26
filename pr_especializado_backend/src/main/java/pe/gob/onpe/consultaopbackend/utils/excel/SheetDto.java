package pe.gob.onpe.consultaopbackend.utils.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetDto {
    private String name;
    private HeaderDto headerDto;
    private List<RowDto> rowDtos;
}
