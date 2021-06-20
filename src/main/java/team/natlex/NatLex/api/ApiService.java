package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<SectionFullDTO> findAllSections() {
        List<SectionFullDTO> sectionDto = new ArrayList<>();
        var sections = sectionRepository.findAll();
        for (Section s : sections) {
            SectionFullDTO sectionFullDTO = new SectionFullDTO();
            sectionFullDTO.setName(s.getName());
            var classes = s.getCodes().stream()
                    .map(geologicalClassRepo::findByCode)
                    .sorted(Comparator.comparing(GeologicalClass::getName))
                    .collect(toList());
            sectionFullDTO.setGeologicalClasses(classes);
            sectionDto.add(sectionFullDTO);
        }
        return sectionDto;

    }

    public List<GeologicalClass> findAllClasses() {
        return geologicalClassRepo.findAll();
    }

    public void createFile() {
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet("Sections sheet");

        var sectionList = sectionRepository.findAll().stream()
                .sorted(Comparator.comparing(Section::getName))
                .collect(toList());
        var geologicalClassList = findAllClasses();

        var classNumbers = geologicalClassList.stream()
                .map(c -> c.getName().charAt(c.getName().length() - 1))
                .distinct()
                .sorted()
                .collect(toList());

        System.out.println(classNumbers);

        drawHeader(classNumbers, sheet);
        drawSections(sectionList, sheet, classNumbers);

        var file = new File("sections_export.xls");
//        file.getParentFile().mkdirs();

        try (FileOutputStream outFile = new FileOutputStream(file)) {
            workbook.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created file: " + file.getAbsolutePath());
    }

    public void drawHeader(List<Character> classNumbers, HSSFSheet sheet) {
        Cell cell;
        Row row;
        row = sheet.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Section name");

        var s = 0;
        for (Character className : classNumbers) {
//            String name = geologicalClass.getName().replaceAll("[^\\d]", "").replaceFirst(".$","");
            cell = row.createCell(s + 1, CellType.STRING);
            cell.setCellValue("Class " + className + " name");

            cell = row.createCell(s + 2, CellType.STRING);
            cell.setCellValue("Class " + className + " code");
            s = s + 2;
        }
    }

    public void drawSections(List<Section> sectionList, HSSFSheet sheet, List<Character> classNumbers) {
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
                var geologicalClass = findClassByCode(codes.get(j));
                var name = geologicalClass.getName();

                System.out.println(name.charAt(name.length() - 1));
                System.out.println(classNumbers.get(i));
                if (name.charAt(name.length() - 1) == classNumbers.get(i)) {
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

    public void readFile() throws IOException {

        FileInputStream inputStream = new FileInputStream(new File("sections_import.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            // Get iterator to all cells of current row
            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> codes = new ArrayList<>();
            List<GeologicalClass> geologicalClassList = new ArrayList<>();
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
                    codes.add(classCode);
                    geologicalClassList.add(new GeologicalClass(className, classCode));
                }
            }
            var section = new Section();
            section.setName(sectionName);
            section.setCodes(codes);
            sectionRepository.save(section);
            for (GeologicalClass gc : geologicalClassList) {
                geologicalClassRepo.save(gc);
            }
        }
    }


    public Section addNewSection(SectionFullDTO sectionFullDTO) {
        if (sectionFullDTO == null) throw new RuntimeException();
        var listOfClasses = sectionFullDTO.getGeologicalClasses();
        var classCodes = listOfClasses.stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        var name = sectionFullDTO.getName();
        var section = sectionRepository.findById(name).orElse(new Section());
        if (sectionRepository.findById(name).isPresent()) {
            var codes = section.getCodes();
            codes.addAll(classCodes);
        } else {
            section.setName(name);
            section.setCodes(classCodes);
        }
        for (GeologicalClass gc : listOfClasses) {
            geologicalClassRepo.save(gc);
        }
        sectionRepository.save(section);
        return section;
    }

    public List<String> findSectionsByCode(String code) {
        return sectionRepository.findSectionsByCode(code);
    }

    public GeologicalClass findClassByCode(String code) {
        return geologicalClassRepo.findByCode(code);
    }

    public void updateSection(SectionFullDTO section, String name) {
        if (sectionRepository.findById(name).isEmpty()) throw new RuntimeException();
        if (section == null) throw new RuntimeException();
        var newSection = new Section();
        newSection.setName(section.getName());
        var codes = section.getGeologicalClasses().stream()
                .map(GeologicalClass::getCode)
                .collect(toList());
        newSection.setCodes(codes);
        sectionRepository.save(newSection);
        var listOfClasses = section.getGeologicalClasses();
        for (GeologicalClass gc : listOfClasses) {
            var sec = geologicalClassRepo.save(gc);
        }
    }

    public void deleteSection(String name) {
        var section = sectionRepository.findById(name).orElseThrow(RuntimeException::new);
        sectionRepository.delete(section);
    }
}
