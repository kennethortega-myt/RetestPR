package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "mae_padron_progreso")
public class MaePadronProgreso {

	@Id
	@Field("n_pagina")
	private Integer pagina;
	
	@Field("b_siguiente")
	private Boolean siguiente;

	@Field("c_aud_usuario_creacion")
	private String audUsuarioCreacion;

	@Field("d_aud_fecha_creacion")
	private Date audFechaCreacion;
}
