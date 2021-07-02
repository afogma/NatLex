package team.natlex.NatLex.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.natlex.NatLex.api.entity.GeologicalClass;

import java.util.List;

@Data
@AllArgsConstructor
public class SectionFullDTO {
    private String name;
    private List<GeologicalClass> geologicalClasses;
}