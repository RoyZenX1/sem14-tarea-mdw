package pe.utp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.model.Celular;

@Repository
public interface CelularRepository extends JpaRepository<Celular, String>{
	//Método para buscar por numero de serie
	Optional<Celular> findByNumeroSerie(String numeroSerie);
	//Verificar si existe
	boolean existsByNumeroSerie(String numeroSerie);
	
	//Eliminar por número serie
	@Modifying
	@Transactional
	@Query("DELETE FROM Celular c WHERE c.numeroSerie = :numeroSerie")
	void deleteByNumeroSerie(@Param("numeroSerie") String numeroSerie);
	
	//Metodos de busqueda personalizada
	List<Celular> findByMarca(String marca);
	//List<Celular> findAlmacenamientoGBGreaterThan(int almacenamiento);
}