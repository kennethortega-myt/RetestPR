package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.service.MaePadronService;
import pe.gob.onpe.pradminbackend.model.dto.padron.PadronDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequestMapping("/padron")
@Validated
@RequiredArgsConstructor
public class PadronController {

	private final MaePadronService maePadronService;
	
	@GetMapping("/mesa/{dni}")
    public ResponseEntity<GenericResponse<PadronDto>> obtenerMesaPorDni(
            @PathVariable("dni")
            @Pattern(regexp = "^[1-9][0-9]*$", message = "dni no valido, tienen que ser numeros")
            @NotBlank(message = "dni es obligatorio")
            @Size(min = 8,max = 8,message = "dni no valido, la longitud debe ser 8")
            String dni
    		) {
        GenericResponse<PadronDto> genericResponse = new GenericResponse<>();
        Optional<PadronDto> padron = this.maePadronService.findByDni(dni);

        if(padron.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(padron.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

	
}
