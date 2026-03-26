package pe.gob.onpe.pradminbackend.security.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;

public class LoginRequest {
	@Size(min = 3, max = 50, message = "El tamaño del Usuario debe estar entre 3 y 50 carateres")
    private String username;
    private String password;
    private String dni;
    private String recaptcha;
    private Boolean bot;
    private Boolean botok;
    private String message;

    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("dni") String dni,
                        @JsonProperty("recaptcha") String recaptcha
    ) {
        this.username = username;
        this.password = password;
        this.dni = dni;
        this.recaptcha = recaptcha;
        this.bot = false;
        this.botok = false;
        this.message = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDni() {
        return dni;
    }

    public String getRecaptcha() {
        return recaptcha;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setRecaptcha(String recaptcha) {
        this.recaptcha = recaptcha;
    }

    public Boolean getBot() {
        return bot;
    }

    public void setBot(Boolean bot) {
        this.bot = bot;
    }

	public Boolean getBotok() {
		return botok;
	}

	public void setBotok(Boolean botok) {
		this.botok = botok;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}