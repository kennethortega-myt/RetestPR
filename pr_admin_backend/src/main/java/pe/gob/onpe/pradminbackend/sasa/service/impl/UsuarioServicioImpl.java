package pe.gob.onpe.pradminbackend.sasa.service.impl;


import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.pradminbackend.sasa.dto.*;
import pe.gob.onpe.pradminbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.pradminbackend.security.dto.PasswordUpdateRequest;
import pe.gob.onpe.pradminbackend.security.dto.RestableceRequest;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class UsuarioServicioImpl implements UsuarioServicio {

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String AUTHORIZATION = "Authorization";

	@Autowired
	@Qualifier("servicioFirma")
	private RestTemplate restTemplate;

	@Value("${sasa.url}")
	private String url;
	
	@Value("${sasa.codigo}")
	private String codigo;
	
	@Override
	public LoginDatosOutputDto accederSistema(LoginInputDto input){
		LoginDatosOutputDto datos = null;
		
				input.setCodigo(codigo);
				input.setRecaptcha("xxxx");
				
				HttpHeaders headers = new HttpHeaders();
				headers.set(CONTENT_TYPE, APPLICATION_JSON);
				HttpEntity<LoginInputDto> requestEntity = new HttpEntity<>(input,
						headers);
				
				try {
						ResponseEntity<LoginDatosOutputDto> responseLogin = restTemplate.exchange(url +"/usuario/loginsc",
						HttpMethod.POST, requestEntity, LoginDatosOutputDto.class);
						datos = responseLogin.getBody();

						
				}catch(Exception e) {
					log.error("ERROR en accederSistema", e);
					throw e;
				}
				
			return datos;
		
	}
		
	@Override
	public CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input,String token) {
	try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, APPLICATION_JSON);
			headers.set(AUTHORIZATION,  token);
			
			HttpEntity<CargarAccesosInputDto> requestEntity = new HttpEntity<>(input,headers);

			ResponseEntity<CargarAccesoDatosOutputDto> responseLogin = restTemplate.exchange(url +"/usuario/cargar-accesos",
					HttpMethod.POST, requestEntity, CargarAccesoDatosOutputDto.class);

			CargarAccesoDatosOutputDto body = responseLogin.getBody();

			if ( null != body && body.getDatos() != null && body.getDatos().getModulos() != null) {
				List<LoginModulosOutputDto> modulosOrdenados = body.getDatos().getModulos()
						.stream()
						.sorted(Comparator.comparingInt(LoginModulosOutputDto::getOrden))
						.toList();
				body.getDatos().setModulos(modulosOrdenados);
			}

			return  responseLogin.getBody();
		} catch (Exception e) {
			log.error("Error en cargarAccesos: ", e);
			throw e;		
		}
	}

	@Override
	public ContraseniaRestableceOutputDto restableceContrasenia(RestableceRequest restableceRequest){
		ContraseniaRestableceInputDto contraseniaRestableceInputDto = new ContraseniaRestableceInputDto(restableceRequest.getUsuario().toUpperCase(), "", this.codigo);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_TYPE, APPLICATION_JSON);
		HttpEntity<ContraseniaRestableceInputDto> requestEntity = new HttpEntity<>(contraseniaRestableceInputDto, headers);
		
		try {
			ResponseEntity<ContraseniaRestableceOutputDto> responseRestablece = restTemplate.exchange(url + "/usuario/olvide-contrasenia", HttpMethod.POST, requestEntity, ContraseniaRestableceOutputDto.class);
			return responseRestablece.getBody();
		} catch (Exception e) {
			log.error("Error en restableceContrasenia: ", e);
			throw e;
		}
	}

	@Override
	public ContraseniaRestableceOutputDto actualizarContrasenia(PasswordUpdateRequest passwordUpdateRequest){
		ContraseniaActualizaInputDto actulizaContrasenia = new ContraseniaActualizaInputDto();
		actulizaContrasenia.setClave(passwordUpdateRequest.getClave());
		
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_TYPE, APPLICATION_JSON);
		headers.set(AUTHORIZATION, "Bearer "+passwordUpdateRequest.getTokenSasa());
		HttpEntity<ContraseniaActualizaInputDto> requestEntity = new HttpEntity<>(actulizaContrasenia, headers);
		try {
			ResponseEntity<ContraseniaRestableceOutputDto> response = restTemplate.exchange(url+"/usuario/actualizar-nueva-clave", HttpMethod.POST, requestEntity, ContraseniaRestableceOutputDto.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error en actualizarContrasenia: ", e);
			throw e;
		}
	}

	@Override
	public void cerrarSesionActivaSasa(String usuario, String token){
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, APPLICATION_JSON);
			headers.set(AUTHORIZATION, "Bearer " + token);

			CerrarSesionInputDto body = new CerrarSesionInputDto();
			body.setUsuario(usuario);
			body.setCodigo(codigo);

			HttpEntity<CerrarSesionInputDto> requestEntity = new HttpEntity<>(body, headers);

			restTemplate.exchange(
					url + "/usuario/cerrar-sesion-activa",
					HttpMethod.POST,
					requestEntity,
					String.class
			);

			log.info("Sesión cerrada en SASA para usuario: {}", usuario);

		} catch (Exception e) {
			log.error("Error al cerrar sesión en SASA para usuario: {}", usuario, e);
		}
	}

}