package pe.gob.onpe.pradminbackend.security.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldErrorModel {
    String fieldName;
    String fieldError;
}
