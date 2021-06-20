package team.natlex.NatLex.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
class SectionFullDTO {
    private String name;
    private List<GeologicalClass> geologicalClasses;
}