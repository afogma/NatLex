package team.natlex.NatLex.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.natlex.NatLex.entity.GeologicalClass;
import team.natlex.NatLex.entity.Section;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.repository.GeologicalClassRepo;
import team.natlex.NatLex.repository.SectionRepo;
import team.natlex.NatLex.exceptions.ClassAlreadyExistsException;
import team.natlex.NatLex.exceptions.GeoClassNotFoundException;
import team.natlex.NatLex.exceptions.SectionNotFoundException;
import team.natlex.NatLex.exceptions.WrongInputException;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ApiService {

    Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final SectionRepo sectionRepo;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<SectionFullDTO> findAllSections() {
        List<SectionFullDTO> sectionDto = new ArrayList<>();
        var sections = sectionRepo.findAll();
        for (Section s : sections) {
            var classes = new ArrayList<>(geologicalClassRepo.findByCodes(s.getCodes()));
            SectionFullDTO sectionFullDTO = new SectionFullDTO(s.getName(), classes);
            sectionDto.add(sectionFullDTO);
        }
        return sectionDto.stream().
                sorted(Comparator.comparing(SectionFullDTO::getName))
                .collect(toList());
    }

    public List<GeologicalClass> findAllClasses() {
        return geologicalClassRepo.findAll();
    }

    public Section addNewSection(SectionFullDTO sectionFullDTO) {
        if (sectionFullDTO == null) throw new WrongInputException();
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        var classCodes = listOfClasses.stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var name = sectionFullDTO.getName();
        var section = sectionRepo.findById(name).orElse(new Section());
        if (sectionRepo.existsById(name)) {
            var codesSet = new LinkedHashSet<>(section.getCodes());
            codesSet.addAll(classCodes);
            section.setCodes(new ArrayList<>(codesSet));
        } else {
            section.setName(name);
            section.setCodes(classCodes);
        }
        for (GeologicalClass gc : listOfClasses) {
            geologicalClassRepo.save(gc);
        }
        sectionRepo.save(section);
        logger.info("{} added", section.getName());
        return section;
    }

    public List<String> findSectionsByCode(String code) {
        return sectionRepo.findSectionsByCode(code);
    }

    public GeologicalClass findClassByCode(String code) {
        return geologicalClassRepo.findByCode(code);
    }

    public Section updateSection(SectionFullDTO sectionFullDTO, String name) {
        if (sectionRepo.findById(name).isEmpty()) throw new SectionNotFoundException();
        if (sectionFullDTO == null) throw new RuntimeException();
        var codes = sectionFullDTO.getGeologicalClasses().stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var newSection = new Section(sectionFullDTO.getName(), codes);
        var savedSection = sectionRepo.save(newSection);
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        for (GeologicalClass gc : listOfClasses) {
            var sec = geologicalClassRepo.save(gc);
        }
        logger.info("{} updated", newSection.getName());
        return savedSection;
    }

    public void deleteSection(String name) {
        if (sectionRepo.findById(name).isEmpty()) throw new SectionNotFoundException();
        sectionRepo.deleteById(name);
        logger.info("{} removed", name);
    }

    public GeologicalClass addNewClass(GeologicalClass geoClass) {
        if (geologicalClassRepo.findById(geoClass.getName()).isPresent()) throw new ClassAlreadyExistsException();
        var clazz = geologicalClassRepo.save(geoClass);
        logger.info("{} added", geoClass.getName());
        return clazz;
    }

    public void deleteClass(String name) {
        if (geologicalClassRepo.findById(name).isEmpty()) throw new GeoClassNotFoundException();
        geologicalClassRepo.deleteById(name);
        logger.info("{} removed", name);
    }

    public GeologicalClass updateClass(GeologicalClass geoClass, String name) {
        if (geologicalClassRepo.findById(name).isEmpty()) throw new GeoClassNotFoundException();
        var clazz = geologicalClassRepo.save(geoClass);
        logger.info("{} updated", geoClass.getName());
        return clazz;
    }
}
