package pe.gob.onpe.consultaopbackend.rest.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import pe.gob.onpe.consultaopbackend.model.dto.CerrarSesionActivaRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.RefreshTokenRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.recaptcha.properties.RecaptchaProperties;
import pe.gob.onpe.consultaopbackend.recaptcha.service.CaptchaServicioV3;
import pe.gob.onpe.consultaopbackend.recaptcha.service.ICaptchaService;
import pe.gob.onpe.consultaopbackend.redis.RedisService;
import pe.gob.onpe.consultaopbackend.sasa.dto.*;
import pe.gob.onpe.consultaopbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.consultaopbackend.security.TokenDecoder;
import pe.gob.onpe.consultaopbackend.security.dto.LoginRequest;
import pe.gob.onpe.consultaopbackend.security.dto.PasswordUpdateRequest;
import pe.gob.onpe.consultaopbackend.security.dto.RestableceRequest;
import pe.gob.onpe.consultaopbackend.security.dto.UserContext;
import pe.gob.onpe.consultaopbackend.security.enums.ErrorCode;
import pe.gob.onpe.consultaopbackend.security.enums.Scopes;
import pe.gob.onpe.consultaopbackend.security.jwt.JWTTokenProvider;
import pe.gob.onpe.consultaopbackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.consultaopbackend.security.jwt.JwtSettings;
import pe.gob.onpe.consultaopbackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.consultaopbackend.security.properties.CifradoProperties;
import pe.gob.onpe.consultaopbackend.security.utils.Util;
import pe.gob.onpe.consultaopbackend.utils.PrConstantes;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Component
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UsuarioServicio usuarioService;
    private final JwtTokenFactory tokenFactory;
    private final ICaptchaService captchaServiceV3;
    private final CifradoProperties cifradoProperties;
    private final RecaptchaProperties recaptchaProperties;
    private final TokenDecoder tokenDecoder;
    private final JWTTokenProvider jwtTokenProvider;
	private final JwtSettings settings;
    private final RedisService redisService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<LoginDatosOutputDto>> authenticate(@RequestBody LoginRequest loginRequest,
                                                                             @RequestHeader(value = PrConstantes.HEADER_IDSESSION, required = false) String idSession,
                                                                             HttpServletRequest request) throws AuthenticationException {
        if (idSession == null || idSession.trim().isEmpty()) {
            GenericResponse<LoginDatosOutputDto> response = GenericResponse.<LoginDatosOutputDto>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("La cabecera IdSession es obligatoria")
                    .errorCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(new Date())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Assert.notNull(loginRequest, "No credentials data provided");
        LoginDatosOutputDto u;
        String recaptcha = loginRequest.getRecaptcha();

        if(recaptchaProperties.validarRecaptcha()) {
            captchaServiceV3.processResponse(recaptcha, CaptchaServicioV3.IMPORTANT_ACTION);
        }

        try {
            LoginInputDto login = new LoginInputDto();
            login.setUsuario(loginRequest.getUsername());
            String decryptedPassword = decrypt(loginRequest.getPassword());
            login.setClave(decryptedPassword);
            u = usuarioService.accederSistema(login);
            validarRespuestaServicioSASA(u);
        } catch (InsufficientAuthenticationException ex) {

            GenericResponse<LoginDatosOutputDto> response = GenericResponse.<LoginDatosOutputDto>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(ex.getMessage())
                    .errorCode(ex.getMessage().equals("Ud. tiene una sesión activa, ¿Desea cerrar la sesión anterior?")? ErrorCode.SESSION_UNICA.getCode() :ErrorCode.AUTHENTICATION.getCode())
                    .timestamp(new Date())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException | ResourceAccessException ex) {

            GenericResponse<LoginDatosOutputDto> response = GenericResponse.<LoginDatosOutputDto>builder()
                    .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .message("Servicio no disponible, vuelva a intentarlo más tarde")
                    .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .timestamp(new Date())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {

            GenericResponse<LoginDatosOutputDto> response = GenericResponse.<LoginDatosOutputDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Ocurrió un error interno. Inténtelo nuevamente más tarde. "
                            + "Si el problema persiste, comuníquese con el área de incidentes.")
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(new Date())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

        UserContext userContext = UserContext.create(String.valueOf(u.getDatos().getUsuario().getIdUsuario()), u.getDatos().getUsuario().getApellidoPaterno().concat(" ").concat(u.getDatos().getUsuario().getApellidoMaterno()).concat(" ").concat(u.getDatos().getUsuario().getNombres()), authorities);
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

        u.getDatos().getUsuario().setToken(accessToken);
        u.getDatos().getUsuario().setRefreshToken(refreshToken);

        clearAuthenticationAttributes(request);

        Claims claimsTokenHeader = this.tokenDecoder.decodeToken(accessToken);
        Claims claimsTokenSasa = this.tokenDecoder.decodeToken(sasaToken);

        Integer sessionUnicaSasa = claimsTokenSasa.get("dato2", Integer.class);
        if(Objects.equals(sessionUnicaSasa, PrConstantes.N_SESSION_UNICA)){
            this.redisService.blacklistAllUserTokens(userContext.getUsernameSinEncriptar());
        }

        long timeExpired = claimsTokenHeader.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.saveUserToken(userContext.getUsernameSinEncriptar(), accessToken, timeExpired);

        GenericResponse<LoginDatosOutputDto> response = GenericResponse.<LoginDatosOutputDto>builder()
                .success(true)
                .message("Login exitoso")
                .data(u)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);

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

    private String decrypt(String encryptedPassword) throws Exception {

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

    @PostMapping("/restablecer-contrasenia")
    public ResponseEntity<ContraseniaRestableceOutputDto> restablecerContrasenia(@RequestBody RestableceRequest restableceRequest) {
        ContraseniaRestableceOutputDto restablece;
        String recaptcha = restableceRequest.getRecaptcha();

        if(recaptchaProperties.validarRecaptcha()) {
            captchaServiceV3.processResponse(recaptcha, CaptchaServicioV3.IMPORTANT_ACTION);
        }
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
            String backendToken =passwordUpdateRequest.getTokenSasa().trim();
            Claims claims = tokenDecoder.decodeToken(backendToken);
            String sasaToken = claims.get("sasa_token", String.class);
            restablece = usuarioService.actualizarContrasenia(passwordUpdateRequest, sasaToken);
            restablece.setResultado(1);
            if(restablece == null) {
                throw new InsufficientAuthenticationException("Usuario no encontrado");
            }
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

    @PostMapping("/cargar-accesos")
    public ResponseEntity<GenericResponse<CargarAccesoDatosOutputDto>> cargarAccesos(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader , @RequestBody @Valid final CargarAccesosInputDto paramInputDto) throws Exception {
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
        
        List<String> scopes = claims.get("scopes", List.class);
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
        userContext.setPerfilId(claimsTokenHeader.get("idp", Integer.class));
        userContext.setUsernameSinEncriptar(claimsTokenHeader.get("usr", String.class));
        userContext.setCodigoOp(claimsTokenHeader.get("codigoOp", String.class));
        userContext.setUsrNumDoc(claimsTokenHeader.get("usrNumDoc", String.class));
        
        String infoPcEncryp = claims.get("cll", String.class);
        String sasaToken = claims.get("sasa_token", String.class);

        String newAccessToken = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp, sasaToken);
        String newRefreshToken = tokenFactory.createRefreshToken(userContext, infoPcEncryp, sasaToken);

        long timeExpired = claimsTokenHeader.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.addToBlacklist(token, timeExpired);

        Claims claimsNewTokenAccess = this.tokenDecoder.decodeToken(newAccessToken);
        long timeExpiredNewTokenAccess = claimsNewTokenAccess.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.saveUserToken(claimsTokenHeader.get("usr", String.class), newAccessToken, timeExpiredNewTokenAccess);

        HashMap<String, Object> response = new HashMap<>();
        
        response.put("resultado", 1);
        response.put("mensaje", "Token refrescado correctamente");
        response.put("token", newAccessToken); // Devolver el nuevo accessToken
        response.put("refreshToken", newRefreshToken); // Devolver el nuevo refreshToken

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/cerrar-sesion-activa")
    public ResponseEntity<GenericResponse<String>> refreshToken(@RequestBody @Valid CerrarSesionActivaRequestDto request) {
        Assert.notNull(request, "No user data provided");

        GenericResponse<String> genericResponse = new GenericResponse<>();

        this.usuarioService.cerrarSesionActivaSasa(request.getUsuario());
        this.redisService.blacklistAllUserTokens(request.getUsuario());

        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión activa cerrada.");
        return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

    }
}
