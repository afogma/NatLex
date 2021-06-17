package team.natlex.NatLex.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GeologicalClassRepo extends JpaRepository<GeologicalClass, String> {

    GeologicalClass findByCode(String code);
}
