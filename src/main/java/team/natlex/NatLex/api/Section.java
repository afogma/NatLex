package team.natlex.NatLex.api;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "SECTIONS")
class Section {

    @Id
    private String name;

    @Column(name = "geo_name")
    private String geoName;

    @Column(name = "geo_code")
    private String geoCode;

    public Section(SectionDTO sectionDTO) {
        this.name = sectionDTO.getName();
        this.geoName = sectionDTO.getGeoClassList().get(0).getName();
        this.geoCode = sectionDTO.getGeoClassList().get(0).getCode();
    }

}
