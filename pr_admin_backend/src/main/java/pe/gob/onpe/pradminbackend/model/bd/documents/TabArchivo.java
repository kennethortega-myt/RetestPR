package pe.gob.onpe.pradminbackend.model.bd.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tab_archivo")
public class TabArchivo extends Auditoria {

	@Id
    private String id;
	
	@Field(name = "n_id_acta")
	private Long idActa;
	
	@Field(name = "n_tipo")
	private Integer tipo;

    @Field("c_guid")
    private String cGuid;

    @Field("c_nombre")
    private String cNombre;

    @Field("c_nombre_original")
    private String cNombreOriginal;

    @Field("c_formato")
    private String cFormato;

    @Field("c_peso")
    private String cPeso;

    @Field("c_ruta")
    private String cRuta;

    @Field("c_nombre_original_anterior")
    private String cNombreOriginalAnterior;
	
}
