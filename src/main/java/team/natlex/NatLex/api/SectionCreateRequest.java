package team.natlex.NatLex.api;

import lombok.Data;

import java.util.List;

@Data
class SectionCreateRequest {
    private String name;
    private List<GeologicalClass> geologicalClasses;
}