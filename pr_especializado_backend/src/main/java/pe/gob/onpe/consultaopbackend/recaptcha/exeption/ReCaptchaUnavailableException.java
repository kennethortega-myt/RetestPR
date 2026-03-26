package pe.gob.onpe.consultaopbackend.recaptcha.exeption;

public class ReCaptchaUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String mensajeInterno;
	private final String mensaje;

	public ReCaptchaUnavailableException(String message) {
		super(message);
		this.mensaje = message;
		this.mensajeInterno = message;
	}

	public ReCaptchaUnavailableException() {
		super();
		this.mensaje = null;
		this.mensajeInterno = null;
	}

	public ReCaptchaUnavailableException(String message, Throwable rootCause) {
		super(message, rootCause);
		this.mensaje = message;
		this.mensajeInterno = message;
	}

	public ReCaptchaUnavailableException(String mensaje, String mensajeInterno) {
		super(mensaje);
		this.mensaje = mensaje;
		this.mensajeInterno = mensajeInterno;
	}

	public ReCaptchaUnavailableException(Throwable rootCause) {
		super(rootCause);
		this.mensaje = null;
		this.mensajeInterno = null;
	}

	public String getMensajeInterno() {
		return mensajeInterno;
	}

	public String getMensaje() {
		return mensaje;
	}
}
