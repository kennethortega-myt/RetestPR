package pe.gob.onpe.consultaopbackend.sasa.service;

import pe.gob.onpe.consultaopbackend.sasa.dto.*;
import pe.gob.onpe.consultaopbackend.security.dto.PasswordUpdateRequest;
import pe.gob.onpe.consultaopbackend.security.dto.RestableceRequest;

public interface UsuarioServicio {
	
  LoginDatosOutputDto accederSistema(LoginInputDto input) throws Exception;

  CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input, String sasaToken ) throws Exception;
  
  ContraseniaRestableceOutputDto restableceContrasenia(RestableceRequest restableceRequest) throws Exception;
  
  ContraseniaRestableceOutputDto actualizarContrasenia(PasswordUpdateRequest passwordUpdateRequest, String sasaToken) throws Exception;

  void cerrarSesionActivaSasa(String usuario);
  
}
