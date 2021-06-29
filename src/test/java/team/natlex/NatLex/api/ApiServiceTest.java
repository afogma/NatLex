package team.natlex.NatLex.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    ApiService apiService = new ApiService(sectionRepo, geologicalClassRepo);

    public Section getSection () {
        Section section = new Section("Section 1", asList("GC11", "GC12", "GC15", "GC17"));
        return section;
    }

    public GeologicalClass getGeoClass() {
        GeologicalClass geologicalClass = new GeologicalClass("Geo Class 11", "GC11");
        return geologicalClass;
    }

    public SectionFullDTO getFullSection() {
        List<GeologicalClass> geologicalClassList = asList(
                new GeologicalClass("Geo Class 11", "GC11"),
                new GeologicalClass("Geo Class 12", "GC12"),
                new GeologicalClass("Geo Class 15", "GC15"),
                new GeologicalClass("Geo Class 17", "GC17"));
        SectionFullDTO sectionFullDTO = new SectionFullDTO("Section 1", geologicalClassList);
        return sectionFullDTO;
    }


    @Test
    void findAllSections() {
        List<Section> sections = asList(getSection());
        when(sectionRepo.findAll()).thenReturn(sections);
        List<Section> sectionList = sectionRepo.findAll();
        assertEquals(sections, sectionList);
        verify(sectionRepo).findAll();
    }

    @Test
    void findAllClasses() {
        List<GeologicalClass> classes = asList(getGeoClass());
        when(geologicalClassRepo.findAll()).thenReturn(classes);
        List<GeologicalClass> geologicalClassList = geologicalClassRepo.findAll();
        assertEquals(classes, geologicalClassList);
        verify(geologicalClassRepo).findAll();
    }

    @Test
    void addNewSection() {
        Section section = getSection();
        when(sectionRepo.save(section)).thenReturn(section);
        Section newSection = apiService.addNewSection(getFullSection());
        assertEquals(section, newSection);
        verify(sectionRepo).save(section);
    }

    @Test
    void findSectionsByCode() {
        List<String> sections = asList(getSection().getName());
        when(sectionRepo.findSectionsByCode("GC11")).thenReturn(sections);
        List<String> sectionList = apiService.findSectionsByCode("GC11");
        assertEquals(sectionList, sections);
    }

    @Test
    void findClassByCode() {
        GeologicalClass geoClass = getGeoClass();
        when(geologicalClassRepo.findByCode("GC11")).thenReturn(geoClass);
        GeologicalClass clazz = apiService.findClassByCode("GC11");
        assertEquals(clazz, geoClass);
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