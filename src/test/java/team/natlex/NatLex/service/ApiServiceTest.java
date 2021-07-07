package team.natlex.NatLex.service;

import org.junit.jupiter.api.Test;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.db.GeologicalClassRepo;
import team.natlex.NatLex.db.SectionRepo;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    ApiService apiService = new ApiService(sectionRepo, geologicalClassRepo);

    @Test
    void findAllSections() {
        var sections = List.of(getSection());
        when(sectionRepo.findAll()).thenReturn(sections);
        var sectionList = sectionRepo.findAll();
        assertEquals(sections, sectionList);
        verify(sectionRepo).findAll();
    }

    @Test
    void findAllClasses() {
        var classes = List.of(getGeoClass());
        when(geologicalClassRepo.findAll()).thenReturn(classes);
        var geologicalClassList = geologicalClassRepo.findAll();
        assertEquals(classes, geologicalClassList);
        verify(geologicalClassRepo).findAll();
    }

    @Test
    void addNewSection() {
        var section = getSection();
        when(sectionRepo.save(section)).thenReturn(section);
        var newSection = apiService.addNewSection(getFullSection());
        assertEquals(section, newSection);
        verify(sectionRepo).save(section);
    }

    @Test
    void findSectionsByCode() {
        var sections = List.of(getSection().getName());
        when(sectionRepo.findSectionsByCode("GC11")).thenReturn(sections);
        var sectionList = apiService.findSectionsByCode("GC11");
        assertEquals(sectionList, sections);
    }

    @Test
    void findClassByCode() {
        var geoClass = getGeoClass();
        when(geologicalClassRepo.findByCode("GC11")).thenReturn(geoClass);
        var clazz = apiService.findClassByCode("GC11");
        assertEquals(clazz, geoClass);
    }

    @Test
    void updateSection() {
        var section = getSection();
        var sectionFullDTO = new SectionFullDTO("Section 1", List.of(new GeologicalClass("Geo Class 14", "GC14")));
        var newSection = new Section("Section 1", List.of("GC11", "GC12", "GC14", "GC15", "GC17"));

        when(sectionRepo.findById("Section 1")).thenReturn(Optional.of(section));
        when(sectionRepo.save(newSection)).thenReturn(newSection);
        apiService.updateSection(sectionFullDTO, "Section 1");
        var sect = sectionRepo.findById("Section 1").orElse(null);
        assertEquals(sect, section);
    }

    @Test
    void deleteSection() {
        var section = getSection();
        when(sectionRepo.findById("Section 1")).thenReturn(Optional.of(section));
        apiService.deleteSection("Section 1");
        assertNotNull(section);
        verify(sectionRepo).deleteById(section.getName());
    }

    @Test
    void addNewClass() {
        var newGeoClass = new GeologicalClass("Geo Class 28", "GC28");
        when(geologicalClassRepo.save(newGeoClass)).thenReturn(newGeoClass);
        var geoClass = apiService.addNewClass(newGeoClass);
        assertEquals(geoClass, newGeoClass);
        verify(geologicalClassRepo).save(newGeoClass);
    }

    @Test
    void deleteClass() {
        var geologicalClass = getGeoClass();
        when(geologicalClassRepo.findById("Geo Class 11")).thenReturn(Optional.of(geologicalClass));
        apiService.deleteClass("Geo Class 11");
        assertNotNull(geologicalClass);
        verify(geologicalClassRepo).deleteById(geologicalClass.getName());
    }

    @Test
    void updateClass() {
        var geoClass = getGeoClass();
        when(geologicalClassRepo.findById("Geo Class 11")).thenReturn(Optional.of(geoClass));
        var newGeoClass = new GeologicalClass("Geo Class 11" , "GC22");
        when(geologicalClassRepo.save(newGeoClass)).thenReturn(newGeoClass);
        var testClass = apiService.updateClass(newGeoClass, "Geo Class 11");
        assertEquals(testClass, newGeoClass);
    }

    private Section getSection() {
        var section = new Section("Section 1", List.of("GC11", "GC12", "GC15", "GC17"));
        return section;
    }

    private GeologicalClass getGeoClass() {
        return new GeologicalClass("Geo Class 11", "GC11");
    }

    private SectionFullDTO getFullSection() {
        List<GeologicalClass> geologicalClassList = List.of(
                new GeologicalClass("Geo Class 11", "GC11"),
                new GeologicalClass("Geo Class 12", "GC12"),
                new GeologicalClass("Geo Class 15", "GC15"),
                new GeologicalClass("Geo Class 17", "GC17"));
        return new SectionFullDTO("Section 1", geologicalClassList);
    }
}