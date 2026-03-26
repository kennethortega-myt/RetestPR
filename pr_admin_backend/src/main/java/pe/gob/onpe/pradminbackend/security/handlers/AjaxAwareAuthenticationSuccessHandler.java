package pe.gob.onpe.pradminbackend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.gob.onpe.pradminbackend.security.dto.UserContext;
import pe.gob.onpe.pradminbackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.pradminbackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.pradminbackend.security.utils.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper;
    private final JwtTokenFactory tokenFactory;

    @Autowired
    public AjaxAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory) {
        this.mapper = mapper;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserContext userContext = (UserContext) authentication.getPrincipal();

        Util util = new Util();
        JwtAuthentication jwt = util.infoPC(request);

        jwt.setUsername(userContext.getUsername());

        String infoPcEncryp = util.encryJson(jwt);

        String accessToken = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp, null);
        String refreshToken = tokenFactory.createRefreshToken(userContext, infoPcEncryp, null);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), tokenMap);

        clearAuthenticationAttributes(request);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}