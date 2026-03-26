package pe.gob.onpe.presentacionbackend.model.bd.repository.secondary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabReporte;

import java.util.List;

public interface TabReporteRepository extends MongoRepository<TabReporte, String> {
	List<TabReporte> findByEstadoInAndFiltro(List<Integer> estado, String filtro );

	Page<TabReporte> findAllByCodigoUsuarioOrderByFechaCreacionDesc(String usuario, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoEleccionOrderByFechaCreacionDesc(String usuario,Integer tipoEleccion, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoReporteOrderByFechaCreacionDesc(String usuario,Integer tipoReporte, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoEleccionAndTipoReporteOrderByFechaCreacionDesc(String usuario,Integer tipoEleccion,Integer tipoReporte, Pageable pageable);
    @Query("{ 'c_codigo': ?0, 'c_filtro_value':  { $regex: ?1, $options: 'i' }, 'n_estado': ?2, 'n_activo': ?3 }")
    Page<TabReporte> findByCodigoUsuarioAndTipoEleccionInFiltroValoresOrderByFechaCreacionDesc(
            String codigoUsuario, String tipoEleccionRegex, Integer estado, Integer nActivo, Pageable pageable);
}
