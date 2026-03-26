package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

public interface CrudService<K> {

    void save(K k);

    void saveAll(List<K> k);

    void deleteAll();
    
    List<K> findAll();

}
