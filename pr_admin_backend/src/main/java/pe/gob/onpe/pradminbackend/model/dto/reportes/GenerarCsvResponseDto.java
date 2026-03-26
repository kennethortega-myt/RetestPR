package pe.gob.onpe.pradminbackend.model.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarCsvResponseDto {
    private String filePath;
}
