package pe.gob.onpe.pradminbackend.recaptcha.service;


import pe.gob.onpe.pradminbackend.recaptcha.exeption.ReCaptchaInvalidException;

public interface ICaptchaService {
	default void processResponse(final String response) throws ReCaptchaInvalidException {}
    default void processResponse(final String response, String action) throws ReCaptchaInvalidException {}
    String getReCaptchaSite();
    String getReCaptchaSecret();
}