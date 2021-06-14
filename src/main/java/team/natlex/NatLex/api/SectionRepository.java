package team.natlex.NatLex.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SectionRepository extends JpaRepository<Section, String> {

    @Query(value = "SELECT name FROM sections WHERE :code=ANY(codes)", nativeQuery = true)
    List<String> findSectionsByCode(String code);

    Section findSectionByName(String name);

}