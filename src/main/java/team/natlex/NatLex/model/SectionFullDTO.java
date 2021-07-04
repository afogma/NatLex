package team.natlex.NatLex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.natlex.NatLex.entity.GeologicalClass;

import java.util.List;

@Data
public class SectionFullDTO {
    private final String name;
    private final List<GeologicalClass> geologicalClasses;
}