package pe.gob.onpe.pradminbackend.utils.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDto {
    private String name;
    private List<SheetDto> sheetDtos;
}
