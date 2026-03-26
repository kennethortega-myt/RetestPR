package pe.gob.onpe.consultaopbackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date timestamp;

    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


}
