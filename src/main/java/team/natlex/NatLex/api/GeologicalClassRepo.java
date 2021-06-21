package team.natlex.NatLex.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GeologicalClassRepo extends JpaRepository<GeologicalClass, String>  {


    GeologicalClass findByCode(String code);

//    @Query(value = "SELECT * FROM classes WHERE :code=ALL(codes)", nativeQuery = true)
    @Query(value = "SELECT * from classes WHERE code IN :codes ORDER BY code", nativeQuery = true)
    List<GeologicalClass> findByCodes (List<String> codes);

}
