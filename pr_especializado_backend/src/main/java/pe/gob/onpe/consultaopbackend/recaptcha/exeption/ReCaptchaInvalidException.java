package pe.gob.onpe.consultaopbackend.recaptcha.exeption;

public class ReCaptchaInvalidException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String mensajeInterno;
	private final String mensaje;

	public ReCaptchaInvalidException(String message) {
		super(message);
		this.mensaje = message;
		this.mensajeInterno = message;
	}

	public ReCaptchaInvalidException() {
		super();
		this.mensaje = null;
		this.mensajeInterno = null;
	}

	public ReCaptchaInvalidException(String message, Throwable rootCause) {
		super(message, rootCause);
		this.mensaje = message;
		this.mensajeInterno = message;
	}

	public ReCaptchaInvalidException(String mensaje, String mensajeInterno) {
		super(mensaje);
		this.mensaje = mensaje;
		this.mensajeInterno = mensajeInterno;
	}

	public ReCaptchaInvalidException(Throwable rootCause) {
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
