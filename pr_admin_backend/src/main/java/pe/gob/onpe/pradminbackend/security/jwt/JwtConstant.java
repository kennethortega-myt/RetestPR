package pe.gob.onpe.pradminbackend.security.jwt;

public class JwtConstant {

    public static final String KEY_VALIDATOR = "wOhb0ceP3hSOf0Lg";
    public static final String KEY = "2g@{4R:BE[e474]7";

    public static final long EXPIRATION_TIME_SECONDS = (long)60 * 60;
    public static final long EXPIRATION_TIME_MILISECONDS = (long)1000 * 60 * 60;// milisegundos - segundos - minutos
    public static final String HEADER_STRING = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private JwtConstant() {
    }

}
