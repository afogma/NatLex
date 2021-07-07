package team.natlex.NatLex.service;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class XlsAdapter {

    List<SectionFullDTO> parseXls(byte[] content) throws Exception {
        var byteArrayInputStream = new ByteArrayInputStream(content);
        var workbook = new HSSFWorkbook(byteArrayInputStream);
        var sheet = workbook.getSheetAt(0);
        List<SectionFullDTO> sectionFullDTOs = new ArrayList<>();

        var rowIterator = sheet.iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            var row = rowIterator.next();
            var cellIterator = row.cellIterator();
            var geologicalClassList = new ArrayList<GeologicalClass>();

            var className = "";
            var classCode = "";
            var sectionName = cellIterator.next().getStringCellValue();
            while (cellIterator.hasNext()) {
                var value = cellIterator.next().getStringCellValue();
                if (!value.isEmpty()) {
                    className = value;
                }
                value = cellIterator.next().getStringCellValue();
                if (!value.isEmpty() && !className.isEmpty()) {
                    classCode = value;
                    geologicalClassList.add(new GeologicalClass(className, classCode));
                }
            }
            sectionFullDTOs.add(new SectionFullDTO(sectionName, geologicalClassList));
        }
        return sectionFullDTOs;
    }

    byte[] xlsExportProcess(List<SectionFullDTO> sectionFullDTOList) throws Exception {
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
        return outFile.toByteArray();
    }

    private void drawHeader(List<String> classNumbers, HSSFSheet sheet) {

        var row = sheet.createRow(0);
        var cell = row.createCell(0, CellType.STRING);
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
