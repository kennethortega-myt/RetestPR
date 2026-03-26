package pe.gob.onpe.pradminbackend.recaptcha.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import pe.gob.onpe.pradminbackend.recaptcha.exeption.ReCaptchaInvalidException;
import pe.gob.onpe.pradminbackend.recaptcha.properties.RecaptchaProperties;

import java.util.regex.Pattern;

@Slf4j
public class AbstractCaptchaServicio implements ICaptchaService{

	
	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected RecaptchaProperties captchaSettings;

	@Autowired
	protected ReCaptchaAttemptServicio reCaptchaAttemptService;

	@Autowired
	protected RestOperations restTemplate;

	protected static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
	protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

	@Override
	public String getReCaptchaSite() {
		return captchaSettings.siteKey();
	}

	@Override
	public String getReCaptchaSecret() {
		return captchaSettings.secretKey();
	}

	protected void securityCheck(final String response) {
		log.debug("Attempting to validate response {}", response);

		if (reCaptchaAttemptService.isBlocked(getClientIP())) {
			throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
		}

		if (!responseSanityCheck(response)) {
			throw new ReCaptchaInvalidException("Response contains invalid characters");
		}
	}

	protected boolean responseSanityCheck(final String response) {
		return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
	}

	protected String getClientIP() {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
