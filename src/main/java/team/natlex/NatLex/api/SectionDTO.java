package team.natlex.NatLex.api;

import lombok.Data;

import java.util.List;

@Data
class SectionDTO {

    private String name;
    private List<GeoClass> geoClassList;

    @Data
    static class GeoClass {
        private String name;
        private String code;
    }
}
