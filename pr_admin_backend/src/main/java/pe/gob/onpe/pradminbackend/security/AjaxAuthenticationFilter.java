package pe.gob.onpe.pradminbackend.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pe.gob.onpe.pradminbackend.model.dto.RefreshTokenRequestDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.redis.RedisService;
import pe.gob.onpe.pradminbackend.sasa.dto.CargarAccesoDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.CargarAccesosInputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.ContraseniaRestableceOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginPerfilesOutputDto;
import pe.gob.onpe.pradminbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.pradminbackend.security.dto.LoginRequest;
import pe.gob.onpe.pradminbackend.security.dto.PasswordUpdateRequest;
import pe.gob.onpe.pradminbackend.security.dto.RestableceRequest;
import pe.gob.onpe.pradminbackend.security.dto.UserContext;
import pe.gob.onpe.pradminbackend.security.enums.ErrorCode;
import pe.gob.onpe.pradminbackend.security.enums.Scopes;
import pe.gob.onpe.pradminbackend.security.jwt.*;
import pe.gob.onpe.pradminbackend.security.properties.CifradoProperties;
import pe.gob.onpe.pradminbackend.security.utils.CryptoUtils;
import pe.gob.onpe.pradminbackend.security.utils.Util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Component
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AjaxAuthenticationFilter {

    private final UsuarioServicio usuarioService;
    private final JwtTokenFactory tokenFactory;
    private final CifradoProperties cifradoProperties;
    private final JWTTokenProvider jwtTokenProvider;
    private final TokenDecoder tokenDecoder;
    private final JwtSettings settings;
    private final RedisService redisService;

    @PostMapping("/login")
    public ResponseEntity<LoginOutputToken> authenticate(@Valid @RequestBody LoginRequest loginRequest,
                                                         @RequestHeader(value = PrConstantes.HEADER_IDSESSION, required = false) String idSession,
                                                         HttpServletRequest request) throws AuthenticationException {

        if (idSession == null || idSession.trim().isEmpty()) {
            return new ResponseEntity<>(LoginOutputToken.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("La cabecera IdSession es obligatoria")
                    .errorCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(new Date())
                    .build(), HttpStatus.BAD_REQUEST);
        }

        Assert.notNull(loginRequest, "No credentials data provided");
        LoginDatosOutputDto u;

        try {
            LoginInputDto login = new LoginInputDto();
            login.setUsuario(loginRequest.getUsername());
            String decryptedPassword = decrypt(loginRequest.getPassword());
            login.setClave(decryptedPassword);
            u = usuarioService.accederSistema(login);
            validarRespuestaServicioSASA(u);
        } catch (InsufficientAuthenticationException ex) {
            return new ResponseEntity<>(LoginOutputToken.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(ex.getMessage())
                    .errorCode(ErrorCode.AUTHENTICATION.getCode())
                    .timestamp(new Date())
                    .build(), HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            return new ResponseEntity<>(LoginOutputToken.builder()
                    .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .message("Servicio no disponible, vuelva a intentarlo más tarde")
                    .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .timestamp(new Date())
                    .build(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
        	log.error("Error inesperado en login: ", e);
            return new ResponseEntity<>(LoginOutputToken.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Ocurrió un error interno. Inténtelo nuevamente más tarde. Si el problema persiste, comuníquese con el área de incidentes.")
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(new Date())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        String abreviaturaPerfil = null;
        LoginPerfilesOutputDto perfil = null;
        String acronimoProceso = null;
        String nombreCc = null;
        String codigoCc = null;
        if(u.getDatos()!=null && u.getDatos().getUsuario()!=null) {
            acronimoProceso = u.getDatos().getUsuario().getAcronimoProceso();
            nombreCc = u.getDatos().getUsuario().getNombreCentroComputo();
            codigoCc = u.getDatos().getUsuario().getCodigoCentroComputo();
        }
        if(u.getDatos()!=null && u.getDatos().getPerfiles()!=null) {
            perfil = u.getDatos().getPerfiles().get(0);
            abreviaturaPerfil = perfil.getAbreviatura();
        }

        SimpleGrantedAuthority s = new SimpleGrantedAuthority(abreviaturaPerfil);
        authorities.add(s);

        UserContext userContext = UserContext.create(CryptoUtils.getHash(loginRequest.getUsername(), "SHA1"), u.getDatos().getUsuario().getApellidoPaterno().concat(" ").concat(u.getDatos().getUsuario().getApellidoMaterno()).concat(" ").concat(u.getDatos().getUsuario().getNombres()), authorities);
        userContext.setUsernameSinEncriptar(loginRequest.getUsername());
        userContext.setUserId(u.getDatos().getUsuario().getIdUsuario());
        userContext.setAcronimoProceso(acronimoProceso);
        userContext.setCodigoCentroComputo(codigoCc);
        userContext.setNombreCentroComputo(nombreCc);
        userContext.setPerfilId(u.getDatos().getPerfiles().get(0).getIdPerfil());
        userContext.setPerfil(u.getDatos().getPerfiles().get(0).getNombre());

        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        userContext.setIdSession(enc.encode(idSession));

        Util util = new Util();
        JwtAuthentication jwt = util.infoPC(request);

        jwt.setUsername(userContext.getUsername());

        String infoPcEncryp = util.encryJson(jwt);        
        String sasaToken = u.getDatos().getUsuario().getToken();

        String accessToken = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp, sasaToken);
        String refreshToken = tokenFactory.createRefreshToken(userContext, infoPcEncryp, sasaToken);

        Claims claimsTokenHeader = this.tokenDecoder.decodeToken(accessToken);
        Claims claimsTokenSasa = this.tokenDecoder.decodeToken(sasaToken);

        Integer sessionUnicaSasa = claimsTokenSasa.get("dato2", Integer.class);
        if(Objects.equals(sessionUnicaSasa, PrConstantes.N_SESSION_UNICA)){
            this.redisService.blacklistAllUserTokens(userContext.getUserId().toString());
        }

        long timeExpired = claimsTokenHeader.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.saveUserToken(userContext.getUserId().toString(), accessToken, timeExpired);

        LoginOutputToken login = LoginOutputToken.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .claveNueva(u.getDatos().getUsuario().getClaveNueva())
                .tokenSasa(sasaToken)
                .build();

        clearAuthenticationAttributes(request);

        return new ResponseEntity<>(login, HttpStatus.OK);

    }

    private void validarRespuestaServicioSASA(LoginDatosOutputDto u) {
        if (u == null)
            throw new InsufficientAuthenticationException("Credenciales incorrectas");
        if (u.getResultado() == -1)
            throw new InsufficientAuthenticationException(u.getMensaje());
        if (null == u.getDatos())
            throw new InsufficientAuthenticationException("El usuario selecionado no cuenta con roles activos.");
        if (null == u.getDatos().getPerfiles() || u.getDatos().getPerfiles().isEmpty())
            throw new InsufficientAuthenticationException("El usuario selecionado no cuenta con roles activos..");
    }

    private final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    
    @PostMapping("/restablecer-contrasenia")
    public ResponseEntity<ContraseniaRestableceOutputDto> restablecerContrasenia(@RequestBody RestableceRequest restableceRequest) {
    	ContraseniaRestableceOutputDto restablece;

    	try {
    		restablece = usuarioService.restableceContrasenia(restableceRequest);
    		if(restablece == null) {
    			throw new InsufficientAuthenticationException("Usuario no encontrado.");
    		}
    		return new ResponseEntity<>(restablece, HttpStatus.OK);
		}catch (InsufficientAuthenticationException iae) {
			restablece = new ContraseniaRestableceOutputDto();
			restablece.setResultado(-1);
			restablece.setMensaje("Usuario no encontrado");
			return new ResponseEntity<>(restablece, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			restablece = new ContraseniaRestableceOutputDto();
			restablece.setResultado(-1);
			restablece.setMensaje("Error al restablecer la contraseña: " + e.getMessage());
			return new ResponseEntity<>(restablece, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @PostMapping("/actualizar-contrasenia")
    public ResponseEntity<ContraseniaRestableceOutputDto> actualizarContrasenia(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
    	ContraseniaRestableceOutputDto restablece = new ContraseniaRestableceOutputDto();
    	try {
    		passwordUpdateRequest.setClave(decrypt(passwordUpdateRequest.getClave()));
        	passwordUpdateRequest.setClave2(decrypt(passwordUpdateRequest.getClave2()));
		} catch (Exception e) {
			restablece.setResultado(-1);
			restablece.setMensaje("Ocurrio un error, vuelva a intentarlo");
			return new ResponseEntity<>(restablece, HttpStatus.BAD_REQUEST);
		}
		if (!passwordUpdateRequest.getClave().equals(passwordUpdateRequest.getClave2())) {
			restablece.setResultado(-1);
			restablece.setMensaje("Las contraseñas no coinciden");
			return new ResponseEntity<>(restablece, HttpStatus.BAD_REQUEST);
		}
    	try {
    		restablece = usuarioService.actualizarContrasenia(passwordUpdateRequest);
    		if(restablece == null) {
    			throw new InsufficientAuthenticationException("Usuario no encontrado");
    		}
            restablece.setResultado(1);
    		return new ResponseEntity<>(restablece, HttpStatus.OK);
    	} catch (InsufficientAuthenticationException iae) {
    		restablece = new ContraseniaRestableceOutputDto();
    		restablece.setResultado(-1);
    		restablece.setMensaje("Usuario no encontrado o token inválido");
    		return new ResponseEntity<>(restablece, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			restablece = new ContraseniaRestableceOutputDto();
			restablece.setResultado(-1);
			restablece.setMensaje("Error al actualizar la contraseña: " + e.getMessage());
			return new ResponseEntity<>(restablece, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    private String decrypt(String encryptedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        String privateKeyPEM = cifradoProperties.llavePrivada().replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey,oaepParams);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    @PostMapping("/cargar-accesos")
    public ResponseEntity<GenericResponse<CargarAccesoDatosOutputDto>> cargarAccesos(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader , @RequestBody @Valid final CargarAccesosInputDto paramInputDto){
        
        GenericResponse<CargarAccesoDatosOutputDto> response = new GenericResponse<>();
        
        String backendToken = jwtTokenProvider.obtenerSoloToken(authorizationHeader).trim();

        Claims claims = tokenDecoder.decodeToken(backendToken);

        String sasaToken = claims.get("sasa_token", String.class);
        
        CargarAccesoDatosOutputDto cargarAccesoDatosOutputDto =  usuarioService.cargarAccesos(paramInputDto, "Bearer " + sasaToken);

        if(cargarAccesoDatosOutputDto == null) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage("No autorizado.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.setSuccess(Boolean.TRUE);
        response.setMessage("La operación se ejecutó correctamente.");
        response.setData(cargarAccesoDatosOutputDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<HashMap<String, Object>> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @RequestBody @Valid RefreshTokenRequestDto request) {

        String token = authorizationHeader.substring(PrConstantes.LENGTH_BEARER);
        Claims claimsTokenHeader = this.tokenDecoder.decodeToken(token);

        HttpHeaders responseHeaders = new HttpHeaders();
        String tokenWithoutBearer = request.getRefreshToken();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())))
                .build()
                .parseSignedClaims(tokenWithoutBearer)
                .getPayload();

        ObjectMapper mapper = new ObjectMapper();
        List<String> scopes = mapper.convertValue(
                claims.get("scopes"),
                new TypeReference<List<String>>() {}
        );
        if (scopes == null || !scopes.contains(Scopes.REFRESH_TOKEN.authority())) {
            throw new InsufficientAuthenticationException("Token inválido. No tiene el scope de refresh.");
        }

        // Reconstruimos la lista de authorities a partir del claim 'per'
        String rol = claims.get("per", String.class);
        if (rol == null || rol.isBlank()) {
            throw new InsufficientAuthenticationException("Token de refresco inválido o malformado: no contiene privilegios.");
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));

        UserContext userContext = UserContext.create(claims.getSubject(), claims.get("usr", String.class), authorities);
        userContext.setAcronimoProceso(claimsTokenHeader.get("apr", String.class));
        userContext.setCodigoCentroComputo(claimsTokenHeader.get("ccc", String.class));
        userContext.setUserId(claimsTokenHeader.get("dil", Integer.class));
        userContext.setIdSession(claimsTokenHeader.get("idSession", String.class));
        userContext.setNombreCentroComputo(claimsTokenHeader.get("ncc", String.class));
        userContext.setPerfil(claimsTokenHeader.get("perfil", String.class));
        userContext.setPerfilId(claimsTokenHeader.get("idp", Integer.class));
        userContext.setUsernameSinEncriptar(claimsTokenHeader.get("usr", String.class));

        String infoPcEncryp = claimsTokenHeader.get("cll", String.class);
        String sasaToken = claimsTokenHeader.get("sasa_token", String.class);

        String newAccessToken = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp, sasaToken);
        String newRefreshToken = tokenFactory.createRefreshToken(userContext, infoPcEncryp, sasaToken);

        long timeExpired = claimsTokenHeader.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.addToBlacklist(token, timeExpired);


        Claims claimsNewTokenAccess = this.tokenDecoder.decodeToken(newAccessToken);
        long timeExpiredNewTokenAccess = claimsNewTokenAccess.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.saveUserToken(claimsTokenHeader.get("dil", Integer.class).toString(), newAccessToken, timeExpiredNewTokenAccess);
        HashMap<String, Object> response = new HashMap<>();

        response.put("resultado", 1);
        response.put("mensaje", "Token refrescado correctamente");
        response.put("token", newAccessToken);
        response.put("refreshToken", newRefreshToken);

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

}
