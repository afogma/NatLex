package team.natlex.NatLex.api;

class Section {

    private String name;
    private String geoName;
    private String geoCode;

    public Section(SectionDTO sectionDTO) {
        this.name = sectionDTO.getName();
        this.geoName = sectionDTO.getGeoClassList().get(0).getName();
        this.geoCode = sectionDTO.getGeoClassList().get(0).getCode();
    }

}
