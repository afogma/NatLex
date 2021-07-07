package team.natlex.NatLex.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.db.GeologicalClassRepo;
import team.natlex.NatLex.db.SectionRepo;
import team.natlex.NatLex.exceptions.ClassAlreadyExistsException;
import team.natlex.NatLex.exceptions.GeoClassNotFoundException;
import team.natlex.NatLex.exceptions.SectionNotFoundException;
import team.natlex.NatLex.exceptions.WrongInputException;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final SectionRepo sectionRepo;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<SectionFullDTO> findAllSections() {
        return sectionRepo.findAll().stream()
                .map(s -> new SectionFullDTO(s.getName(), geologicalClassRepo.findByCodes(s.getCodes())))
                .sorted(Comparator.comparing(SectionFullDTO::getName))
                .collect(toList());
    }

    public List<String> findSectionsByCode(String code) {
        return sectionRepo.findSectionsByCode(code);
    }

    public GeologicalClass findClassByCode(String code) {
        return geologicalClassRepo.findByCode(code);
    }

    public Section addNewSection(SectionFullDTO sectionFullDTO) {
        if (sectionFullDTO == null) throw new WrongInputException();
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        var classCodes = listOfClasses.stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var name = sectionFullDTO.getName();
        var section = sectionRepo.findById(name).orElse(new Section());
        if (sectionRepo.findById(name).isPresent()) {
            var codesSet = new LinkedHashSet<>(section.getCodes());
            codesSet.addAll(classCodes);
            section.setCodes(new ArrayList<>(codesSet));
        } else {
            section.setName(name);
            section.setCodes(classCodes);
        }
        geologicalClassRepo.saveAll(listOfClasses);
        sectionRepo.save(section);
        logger.info("{} added", section.getName());
        return section;
    }

    public Section updateSection(SectionFullDTO sectionFullDTO, String name) {
        if (sectionRepo.findById(name).isEmpty()) throw new SectionNotFoundException();
        var codes = sectionFullDTO.getGeologicalClasses().stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var newSection = new Section(sectionFullDTO.getName(), codes);
        var savedSection = sectionRepo.save(newSection);
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        geologicalClassRepo.saveAll(listOfClasses);
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

    public GeologicalClass updateClass(GeologicalClass geoClass, String name) {
        if (geologicalClassRepo.findById(name).isEmpty()) throw new GeoClassNotFoundException();
        var clazz = geologicalClassRepo.save(geoClass);
        logger.info("{} updated", geoClass.getName());
        return clazz;
    }

    public void deleteClass(String name) {
        if (geologicalClassRepo.findById(name).isEmpty()) throw new GeoClassNotFoundException();
        geologicalClassRepo.deleteById(name);
        logger.info("{} removed", name);
    }
}
