package team.natlex.NatLex.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeologicalClassRepo extends JpaRepository<GeologicalClass, String> {

    GeologicalClass findByCode(String code);

    @Query(value = "SELECT * from classes WHERE code IN :codes ORDER BY code", nativeQuery = true)
    List<GeologicalClass> findByCodes(List<String> codes);
}
