package team.natlex.NatLex.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ApiRepository extends JpaRepository<Section, String> {

//    @Query(nativeQuery = true, value = "SELECT name from SECTIONS where codes = ANY(:code)")
    @Query(value = "select name, array_contains( (:code,), sections.codes) from sections", nativeQuery = true)
//    @Query(value = "select name from sections s where exists (array_contains(s.codes, (code',)))", nativeQuery = true)
//    @Query(value = "SELECT name FROM SECTIONS WHERE sections.codes @> codes[code]", nativeQuery = true)
//    @Query (nativeQuery = true, value = "select name from sections s where (s.codes ':code')")
//@Query(value = "select name from sections s where array_contains(s.codes, 'GC11')", nativeQuery = true)
    List<String> findSectionsByCode(String code);

}
