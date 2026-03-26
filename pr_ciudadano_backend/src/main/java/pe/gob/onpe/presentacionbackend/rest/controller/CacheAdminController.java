package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
public class CacheAdminController {

    private final CacheManager cacheManager;

    /**
     * Limpia todas las cachés de ubigeos
     */
    @DeleteMapping("/ubigeos")
    public ResponseEntity<String> clearUbigeoCaches() {
        clearCache("ubigeos_departamento_cache");
        clearCache("ubigeos_provincias_cache");
        clearCache("ubigeos_distritos_cache");
        clearCache("ubigeos_local_votacion_cache");
        clearCache("ubigeos_dep_prov_dist_cache");
        
        return ResponseEntity.ok("Cachés de ubigeos limpiadas exitosamente");
    }

    /**
     * Limpia una caché específica por nombre
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<String> clearSpecificCache(@PathVariable String cacheName) {
        if (clearCache(cacheName)) {
            return ResponseEntity.ok("Caché '" + cacheName + "' limpiada exitosamente");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Limpia todas las cachés de la aplicación
     */
    @DeleteMapping("/all")
    public ResponseEntity<String> clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> clearCache(cacheName));
        
        return ResponseEntity.ok("Todas las cachés limpiadas exitosamente");
    }

    private boolean clearCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return true;
        }
        return false;
    }
}
