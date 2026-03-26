package pe.gob.onpe.pradminbackend.sasa.service;

import pe.gob.onpe.pradminbackend.sasa.dto.CargarAccesoDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.CargarAccesosInputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.ContraseniaRestableceOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.pradminbackend.security.dto.PasswordUpdateRequest;
import pe.gob.onpe.pradminbackend.security.dto.RestableceRequest;

public interface UsuarioServicio {
	
  LoginDatosOutputDto accederSistema(LoginInputDto input);

  CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input, String token );
  
  ContraseniaRestableceOutputDto restableceContrasenia(RestableceRequest restableceRequest);
  
  ContraseniaRestableceOutputDto actualizarContrasenia(PasswordUpdateRequest passwordUpdateRequest);

  void cerrarSesionActivaSasa(String usuario, String token);
  
}
