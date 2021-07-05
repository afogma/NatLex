package team.natlex.NatLex.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.natlex.NatLex.db.Section;

import java.util.List;

@Repository
public interface SectionRepo extends JpaRepository<Section, String> {

    @Query(value = "SELECT name FROM sections WHERE :code=ANY(codes) ORDER BY name", nativeQuery = true)
    List<String> findSectionsByCode(String code);

}
