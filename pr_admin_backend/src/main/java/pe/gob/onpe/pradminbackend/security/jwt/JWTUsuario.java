package pe.gob.onpe.pradminbackend.security.jwt;

public record JWTUsuario(
        Integer idUsuario,
        Integer idAplicacion,
        String numeroDocumento,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        Integer tipoSesion,
        String idSesion
) {}