package team.natlex.NatLex.api;

import lombok.Data;

import java.util.List;

@Data
class SectionFullDTO {
    private String name;
    private List<GeologicalClass> geologicalClasses;
}