package team.natlex.NatLex.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.natlex.NatLex.api.entity.GeologicalClass;

import java.util.List;


public interface GeologicalClassRepo extends JpaRepository<GeologicalClass, String>  {


    GeologicalClass findByCode(String code);

    @Query(value = "SELECT * from classes WHERE code IN :codes ORDER BY code", nativeQuery = true)
    List<GeologicalClass> findByCodes (List<String> codes);

}
