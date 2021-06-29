package team.natlex.NatLex.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ApiServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    ApiService apiService = new ApiService(sectionRepo, geologicalClassRepo);

    public Section getSection () {
        Section section = new Section("Section 1", Arrays.asList("GC11", "GC12", "GC15", "GC17"));
        return section;
    }

    public GeologicalClass getGeoClass() {
        GeologicalClass geologicalClass = new GeologicalClass("Geo Class 11", "GC11");
        return geologicalClass;
    }


    @Test
    void findAllSections() {
    }

    @Test
    void findAllClasses() {
    }

    @Test
    void addNewSection() {
    }

    @Test
    void findSectionsByCode() {
    }

    @Test
    void findClassByCode() {
    }

    @Test
    void updateSection() {
    }

    @Test
    void deleteSection() {
    }

    @Test
    void addNewClass() {
    }

    @Test
    void deleteClass() {
    }

    @Test
    void updateClass() {
    }
}