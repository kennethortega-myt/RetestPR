package pe.gob.onpe.pradminbackend.security;

public interface TokenExtractor {
    String extract(String payload);
}
