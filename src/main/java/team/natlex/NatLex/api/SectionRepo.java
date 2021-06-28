package team.natlex.NatLex.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionRepo extends JpaRepository<Section, String> {

    @Query(value = "SELECT name FROM sections WHERE :code=ANY(codes) ORDER BY name", nativeQuery = true)
    List<String> findSectionsByCode(String code);

}
