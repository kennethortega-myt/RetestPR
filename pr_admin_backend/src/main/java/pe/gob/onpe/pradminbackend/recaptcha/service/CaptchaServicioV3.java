package pe.gob.onpe.pradminbackend.recaptcha.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import pe.gob.onpe.pradminbackend.recaptcha.dto.GoogleOutputDto;
import pe.gob.onpe.pradminbackend.recaptcha.exeption.ReCaptchaInvalidException;
import pe.gob.onpe.pradminbackend.recaptcha.exeption.ReCaptchaUnavailableException;

import java.net.URI;


@Slf4j
@Service("captchaServiceV3")
public class CaptchaServicioV3 extends AbstractCaptchaServicio{
	    public static final String REGISTER_ACTION = "register";
	    public static final String IMPORTANT_ACTION = "importantAction";
	    
	    @Override
	    public void processResponse(String response, final String action) throws ReCaptchaInvalidException {
	        securityCheck(response);
	        
	        final URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE, getReCaptchaSecret(), response, "localhost"));
	        try {
	        	log.info("INGRESE: 1");
	        	log.info("INGRESE: URL" + verifyUri);
	        	
	        	final GoogleOutputDto googleResponse = restTemplate.getForObject(verifyUri, GoogleOutputDto.class);
	        	log.info("INGRESE: 2");
	        	log.info("Google's response: {} ", googleResponse);

	        	log.info("IS SUCCESS: " + googleResponse.isSuccess());
	        	log.info("ACTION 1: " + googleResponse.getAction());
	            log.info("ACTION 2: " + action);
	            log.info("STORE 1: " + googleResponse.getScore());
	            log.info("TRESHOLD: " + captchaSettings.threshold());
	            log.info("STORE 2: " + captchaSettings.threshold());
	            if (!googleResponse.isSuccess() || !googleResponse.getAction().equals(action) || googleResponse.getScore() < Float.valueOf(captchaSettings.threshold())) {
	                if (googleResponse.hasClientError()) {
	                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
	                }
	                throw new ReCaptchaInvalidException("reCaptcha no se validó con éxito.");
	            }
	        } catch (RestClientException rce) {
	        	log.error("error en CaptchaServicioV3 processResponse", rce);
	            throw new ReCaptchaUnavailableException("El registro no está disponible en este momento.  Inténtelo de nuevo más tarde.",rce.getMessage());
	        }
	        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
	    }
}
