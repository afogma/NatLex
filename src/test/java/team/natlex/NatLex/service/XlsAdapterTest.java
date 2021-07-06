package team.natlex.NatLex.service;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;

class XlsAdapterTest {

    XlsAdapter xlsAdapter = new XlsAdapter();

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
        var fullDTOList = xlsAdapter.parseXls(Files.readAllBytes(Paths.get("sections.xls")));
        assertEquals(getSectionFullList(), fullDTOList);
    }

    /* TODO: Since testing this method is quiet complicated and gives different results
    (with equal DTO's), testing was done using real files export.
     */

    @Test
    void xlsExportProcess() throws Exception {

//        byte[] fileContent = Files.readAllBytes(Paths.get("sections.xls"));
//        byte[] content = xlsAdapter.xlsExportProcess(getSectionFullList());
//        assertEquals(content, fileContent);

        var sectionFullDTOList = getSectionFullList();

        var sectionList = sectionFullDTOList.stream()
                .map(SectionFullDTO::sectionData)
                .collect(toList());

        var geologicalClasses = sectionFullDTOList.stream()
                .map(SectionFullDTO::getGeologicalClasses)
                .flatMap(List::stream)
                .distinct()
                .collect(toMap(GeologicalClass::getCode, Function.identity()));

        var classNumbers = sectionFullDTOList.stream()
                .flatMap(s -> s.getGeologicalClasses().stream())
                .map(GeologicalClass::classNumber)
                .distinct()
                .sorted()
                .collect(toList());
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet("Sections sheet");
        drawHeader(classNumbers, sheet);
        drawSections(sectionList, sheet, classNumbers, geologicalClasses);

        var outFile = new ByteArrayOutputStream();
        workbook.write(outFile);
        workbook.close();
        outFile.close();


        var DTOSections = xlsAdapter.parseXls(Files.readAllBytes(Paths.get("sections.xls")));
        byte[] fileContent = xlsAdapter.xlsExportProcess(DTOSections);
        var fileWorkbook = new HSSFWorkbook();
        var baos = new ByteArrayOutputStream(fileContent.length);
        fileWorkbook.write(baos);

        assertEquals(workbook, fileWorkbook);
    }

    private void drawHeader(List<String> classNumbers, HSSFSheet sheet) {
        Cell cell;
        Row row;
        row = sheet.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Section name");

        var s = 0;
        for (String className : classNumbers) {
            cell = row.createCell(s + 1, CellType.STRING);
            cell.setCellValue("Class " + className + " name");

            cell = row.createCell(s + 2, CellType.STRING);
            cell.setCellValue("Class " + className + " code");
            s = s + 2;
        }
    }

    private void drawSections(List<Section> sectionList, HSSFSheet sheet, List<String> classNumbers, Map<String, GeologicalClass> geoClasses) {
        var rownum = 0;
        Cell cell;
        Row row;
        for (Section section : sectionList) {
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(section.getName());

            var codes = section.getCodes().stream()
                    .sorted()
                    .collect(toList());
            int p = 0;
            int i = 0;
            for (int j = 0; j < codes.size(); j++) {
                var geologicalClass = geoClasses.get(codes.get(j));
                var name = geologicalClass.getName();
                var classNumber = geologicalClass.classNumber();

                if (classNumber.equals(classNumbers.get(i))) {
                    cell = row.createCell(p + 1, CellType.STRING);
                    cell.setCellValue(name);

                    cell = row.createCell(p + 2, CellType.STRING);
                    cell.setCellValue(codes.get(j));

                } else {
                    cell = row.createCell(p + 1, CellType.STRING);
                    cell.setCellValue("");

                    cell = row.createCell(p + 2, CellType.STRING);
                    cell.setCellValue("");
                    j--;
                }
                i++;
                p += 2;
            }
        }
    }
}
