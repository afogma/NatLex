package team.natlex.NatLex.model;

import lombok.Data;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class SectionFullDTO {
    private final String name;
    private final List<GeologicalClass> geologicalClasses;

    public Section getSection() {
        var codes = geologicalClasses.stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        return new Section(name, codes);
    }
}