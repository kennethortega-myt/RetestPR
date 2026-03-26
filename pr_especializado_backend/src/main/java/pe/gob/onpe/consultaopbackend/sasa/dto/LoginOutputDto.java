package pe.gob.onpe.consultaopbackend.sasa.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOutputDto {
	
	private List<LoginPerfilesOutputDto> perfiles;
	private LoginUsuarioOutputDto usuario;
}
