package pe.gob.onpe.pradminbackend.security.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    GLOBAL(2),

    AUTHENTICATION(10), JWT_TOKEN_EXPIRED(11);

    private final int code;

    ErrorCode(int errorCode) {
        this.code = errorCode;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}

