package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<SectionFullDTO> findAllSections() {
        List<SectionFullDTO> sectionDto = new ArrayList<>();
        var sections = sectionRepository.findAll();
        for (Section s : sections) {
            var classes = geologicalClassRepo.findByCodes(s.getCodes()).stream()
                    .sorted(Comparator.comparing(GeologicalClass::getCode))
                    .collect(toList());
            SectionFullDTO sectionFullDTO = new SectionFullDTO(s.getName(), classes);
            sectionDto.add(sectionFullDTO);
        }
        return sectionDto;
    }

    public List<GeologicalClass> findAllClasses() {
        return geologicalClassRepo.findAll();
    }

    public Section addNewSection(SectionFullDTO sectionFullDTO) {
        if (sectionFullDTO == null) throw new RuntimeException();
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        var classCodes = listOfClasses.stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var name = sectionFullDTO.getName();
        var section = sectionRepository.findById(name).orElse(new Section());
        if (sectionRepository.findById(name).isPresent()) {
            var codes = section.getCodes();
            codes.addAll(classCodes);
        } else {
            section.setName(name);
            section.setCodes(classCodes);
        }
        for (GeologicalClass gc : listOfClasses) {
            geologicalClassRepo.save(gc);
        }
        sectionRepository.save(section);
        return section;
    }

    public List<String> findSectionsByCode(String code) {
        return sectionRepository.findSectionsByCode(code);
    }

    public GeologicalClass findClassByCode(String code) {
        return geologicalClassRepo.findByCode(code);
    }

    public void updateSection(SectionFullDTO section, String name) {
        if (sectionRepository.findById(name).isEmpty()) throw new RuntimeException();
        if (section == null) throw new RuntimeException();
        var codes = section.getGeologicalClasses().stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var newSection = new Section(section.getName(), codes);
        sectionRepository.save(newSection);
        var listOfClasses = section.getGeologicalClasses();
        for (GeologicalClass gc : listOfClasses) {
            var sec = geologicalClassRepo.save(gc);
        }
    }

    public void deleteSection(String name) {
        sectionRepository.deleteById(name);
    }
}
