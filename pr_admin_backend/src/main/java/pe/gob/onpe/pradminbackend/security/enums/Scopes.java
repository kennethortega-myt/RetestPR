package pe.gob.onpe.pradminbackend.security.enums;

public enum Scopes {
    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
