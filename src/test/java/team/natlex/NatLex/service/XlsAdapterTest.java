package team.natlex.NatLex.service;

import org.junit.jupiter.api.Test;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.model.SectionFullDTO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsAdapterTest {

    XlsAdapter xlsAdapter = new XlsAdapter();

    @Test
    void parseXls() throws Exception {
        var fullDTOList = xlsAdapter.parseXls(Files.readAllBytes(Paths.get("sections.xls")));
        assertEquals(getSectionFullList(), fullDTOList);
    }

    /* TODO: Since testing this method is quiet complicated and gives different results
         (with equal DTO's, byte arrays and workbooks are still differs), testing was done using real files export.
     */
    @Test
    void xlsExportProcess() throws Exception {

    }

    private List<SectionFullDTO> getSectionFullList() {
        var geoClassess1 = List.of(new GeologicalClass("Geo Class 11", "GC11"),
                new GeologicalClass("Geo Class 12", "GC12"),
                new GeologicalClass("Geo Class 13", "GC13"));

        var geoClassess2 = List.of(new GeologicalClass("Geo Class 21", "GC21"),
                new GeologicalClass("Geo Class 23", "GC23"));
        var geoClassess3 = List.of(new GeologicalClass("Geo Class 31", "GC31"),
                new GeologicalClass("Geo Class 32", "GC32"));
        var sectionFullDTOs1 = new SectionFullDTO("Section 1", geoClassess1);
        var sectionFullDTOs2 = new SectionFullDTO("Section 2", geoClassess2);
        var sectionFullDTOs3 = new SectionFullDTO("Section 3", geoClassess3);
        List<SectionFullDTO> sectionFullDTOList = List.of(sectionFullDTOs1, sectionFullDTOs2, sectionFullDTOs3);
        return sectionFullDTOList;
    }
}
