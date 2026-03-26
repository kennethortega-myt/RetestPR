package pe.gob.onpe.consultaopbackend.model.bd.repository.secondary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;

import java.util.List;

public interface TabReporteRepository extends MongoRepository<TabReporte, String> {
	List<TabReporte> findByEstadoInAndFiltro(List<Integer> estado, String filtro );

	Page<TabReporte> findAllByCodigoUsuarioAndEstadoNotInOrderByFechaCreacionDesc(String usuario, List<Integer> estados, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoEleccionAndEstadoNotInOrderByFechaCreacionDesc(String usuario, Integer tipoEleccion, List<Integer> estados, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoReporteAndEstadoNotInOrderByFechaCreacionDesc(String usuario, Integer tipoReporte, List<Integer> estados, Pageable pageable);
	Page<TabReporte> findAllByCodigoUsuarioAndTipoEleccionAndTipoReporteAndEstadoNotInOrderByFechaCreacionDesc(String usuario, Integer tipoEleccion, Integer tipoReporte, List<Integer> estados, Pageable pageable);
    @Query("{ 'c_codigo': ?0, 'c_filtro_value': { $regex: ?1 } }")
    Page<TabReporte> findByCodigoUsuarioAndTipoEleccionInFiltroValoresOrderByFechaCreacionDesc(
            String codigoUsuario, String tipoEleccionRegex, Pageable pageable);

}
