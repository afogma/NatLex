package team.natlex.NatLex.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.model.XlsJob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class XlsAdapterTest {

    XlsAdapter xlsAdapter = mock(XlsAdapter.class);

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
        List<SectionFullDTO> sectionFullDTOList = new ArrayList<>();
        sectionFullDTOList.add(sectionFullDTOs1);
        sectionFullDTOList.add(sectionFullDTOs2);
        sectionFullDTOList.add(sectionFullDTOs3);
        return sectionFullDTOList;
    }


    @Test
    void parseXls() throws Exception {
//        var multipartFile = new MockMultipartFile("file", "sections.xls".getBytes());
//        var job = new XlsJob(multipartFile.getBytes());
        when(xlsAdapter.parseXls("sections.xls".getBytes())).thenReturn(getSectionFullList());
        var fullDTOList = xlsAdapter.parseXls("sections.xls".getBytes());
        assertEquals(getSectionFullList(), fullDTOList);
    }

    @Test
    void xlsExportProcess() throws Exception {

        byte[] fileContent = "section.xls".getBytes();
        when(xlsAdapter.xlsExportProcess(getSectionFullList())).thenReturn(fileContent);
        byte[] content = xlsAdapter.xlsExportProcess(getSectionFullList());
        assertEquals(content, fileContent);
    }
}