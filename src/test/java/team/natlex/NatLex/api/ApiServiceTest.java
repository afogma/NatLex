package team.natlex.NatLex.api;

import org.junit.jupiter.api.Test;
import team.natlex.NatLex.api.entity.GeologicalClass;
import team.natlex.NatLex.api.entity.Section;
import team.natlex.NatLex.api.model.SectionFullDTO;
import team.natlex.NatLex.api.repository.GeologicalClassRepo;
import team.natlex.NatLex.api.repository.SectionRepo;
import team.natlex.NatLex.api.service.ApiService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    ApiService apiService = new ApiService(sectionRepo, geologicalClassRepo);

    public Section getSection() {
        Section section = new Section("Section 1", asList("GC11", "GC12", "GC15", "GC17"));
        return section;
    }

    public GeologicalClass getGeoClass() {
        return new GeologicalClass("Geo Class 11", "GC11");
    }

    public SectionFullDTO getFullSection() {
        List<GeologicalClass> geologicalClassList = asList(
                new GeologicalClass("Geo Class 11", "GC11"),
                new GeologicalClass("Geo Class 12", "GC12"),
                new GeologicalClass("Geo Class 15", "GC15"),
                new GeologicalClass("Geo Class 17", "GC17"));
        return new SectionFullDTO("Section 1", geologicalClassList);
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
        Section section = getSection();
        SectionFullDTO sectionFullDTO = new SectionFullDTO("Section 1", asList(new GeologicalClass("Geo Class 14", "GC14")));
        Section newSection = new Section("Section 1", asList("GC11", "GC12", "GC14", "GC15", "GC17"));

        when(sectionRepo.findById("Section 1")).thenReturn(java.util.Optional.ofNullable(section));
        when(sectionRepo.save(newSection)).thenReturn(newSection);
        apiService.updateSection(sectionFullDTO, "Section 1");
        Section sect = sectionRepo.findById("Section 1").orElse(null);
        assertEquals(sect, section);
    }

    @Test
    void deleteSection() {
        Section section = getSection();
        when(sectionRepo.findById("Section 1")).thenReturn(java.util.Optional.ofNullable(section));
        apiService.deleteSection("Section 1");
        assert section != null;
        verify(sectionRepo).deleteById(section.getName());
    }

    @Test
    void addNewClass() {
        GeologicalClass newGeoClass = new GeologicalClass("Geo Class 28", "GC28");
        when(geologicalClassRepo.save(newGeoClass)).thenReturn(newGeoClass);
        GeologicalClass geoClass = apiService.addNewClass(newGeoClass);
        assertEquals(geoClass, newGeoClass);
        verify(geologicalClassRepo).save(newGeoClass);
    }

    @Test
    void deleteClass() {
        GeologicalClass geologicalClass = getGeoClass();
        when(geologicalClassRepo.findById("Geo Class 11")).thenReturn(java.util.Optional.ofNullable(geologicalClass));
        apiService.deleteClass("Geo Class 11");
        assert geologicalClass != null;
        verify(geologicalClassRepo).deleteById(geologicalClass.getName());
    }

    @Test
    void updateClass() {
        GeologicalClass geoClass = getGeoClass();
        when(geologicalClassRepo.findById("Geo Class 11")).thenReturn(java.util.Optional.ofNullable(geoClass));
        GeologicalClass newGeoClass = new GeologicalClass("Geo Class 11" , "GC22");
        when(geologicalClassRepo.save(newGeoClass)).thenReturn(newGeoClass);
        GeologicalClass testClass = apiService.updateClass(newGeoClass, "Geo Class 11");
        assertEquals(testClass, newGeoClass);
    }
}