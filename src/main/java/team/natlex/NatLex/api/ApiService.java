package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
class ApiService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<Section> findAllSections() {
        return sectionRepository.findAll();
    }

    public List<GeologicalClass> findAllClasses() {
        return geologicalClassRepo.findAll();
    }

    private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    public void createFile() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sections sheet");

        List<Section> sectionList = findAllSections();
        List<GeologicalClass> geologicalClassList = findAllClasses();

        int rownum = 0;
        Cell cell;
        Row row;
        //
        HSSFCellStyle style = createStyleForTitle(workbook);

        row = sheet.createRow(rownum);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Section name");
        cell.setCellStyle(style);

        for (int i = 0, k = 0; i < geologicalClassList.size(); i++) {
            cell = row.createCell(k + 1, CellType.STRING);
            cell.setCellValue("Class " + (i + 1) + " name");
//            cell.setCellStyle(style);

            cell = row.createCell(k + 2, CellType.STRING);
            cell.setCellValue("Class " + (i + 1) + " code");
//            cell.setCellStyle(style);
            k = k + 2;
        }


        for (Section section : sectionList) {
//            List<Section> listOfClasses = findAllClassesBySections(section.getName());
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(section.getName());
            int k = 0;
//            for (GeologicalClass clazz : geologicalClassList) {
//            for (Section sect : listOfClasses) {
//                cell = row.createCell(k + 1, CellType.STRING);
//                cell.setCellValue(sect.getClassName());

//                cell = row.createCell(k + 2, CellType.STRING);
//                cell.setCellValue(sect.getClassCode());
//                k = k + 2;
//            }
        }
        File file = new File("sections.xls");
//        file.getParentFile().mkdirs();


        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            workbook.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created file: " + file.getAbsolutePath());
    }

    public Section addNewSection(SectionCreateRequest sectionRequest) {
        if (sectionRequest == null) throw new RuntimeException();
        List<GeologicalClass> listOfClasses = sectionRequest.getGeologicalClasses();
        List<String> classCodes = listOfClasses.stream()
                .map(c -> c.getCode())
                .collect(toList());
        String name = sectionRequest.getName();
        Section section = new Section();
        if (sectionRepository.findSectionByName(name) != null) {
            section = sectionRepository.findSectionByName(name);
            List<String> codes = section.getCodes();
            codes.addAll(classCodes);
        } else {
            section.setName(sectionRequest.getName());
            section.setCodes(classCodes);
        }
        for (GeologicalClass gc : listOfClasses) {
            var sec = geologicalClassRepo.save(gc);
        }

        System.out.println("================");
        System.out.println(section);
        System.out.println("================");
        sectionRepository.save(section);
        return section;
    }

    public List<String> findSectionsByCode(String classCode) {
        return sectionRepository.findSectionsByCode(classCode);
    }

}
