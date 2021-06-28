package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.natlex.NatLex.exceptions.ClassAlreadyExistsException;
import team.natlex.NatLex.exceptions.GeoClassNotFoundException;
import team.natlex.NatLex.exceptions.SectionNotFoundException;
import team.natlex.NatLex.exceptions.WrongInputException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            var codes = section.getCodes();
            codes.addAll(classCodes);
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

    public void updateSection(SectionFullDTO section, String name) {
        if (sectionRepo.findById(name).isEmpty()) throw new SectionNotFoundException();
        if (section == null) throw new RuntimeException();
        var codes = section.getGeologicalClasses().stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var newSection = new Section(section.getName(), codes);
        sectionRepo.save(newSection);
        var listOfClasses = section.getGeologicalClasses();
        for (GeologicalClass gc : listOfClasses) {
            var sec = geologicalClassRepo.save(gc);
        }
        logger.info("{} updated", newSection.getName());
    }

    public void deleteSection(String name) {
        if(!sectionRepo.existsById(name)) throw new SectionNotFoundException();
        sectionRepo.deleteById(name);
        logger.info("{} removed", name);
    }

    public void addNewClass(GeologicalClass geoClass) {
        if (geologicalClassRepo.existsById(geoClass.getName())) throw new ClassAlreadyExistsException();
        geologicalClassRepo.save(geoClass);
        logger.info("{} added", geoClass.getName());
    }

    public void deleteClass(String name) {
        if(!geologicalClassRepo.existsById(name)) throw new GeoClassNotFoundException();
        geologicalClassRepo.deleteById(name);
        logger.info("{} removed", name);
    }

    public void updateClass(GeologicalClass geoClass, String name) {
        if (!geologicalClassRepo.existsById(name)) throw new GeoClassNotFoundException();
        geologicalClassRepo.save(geoClass);
        logger.info("{} updated", geoClass.getName());
    }
}
