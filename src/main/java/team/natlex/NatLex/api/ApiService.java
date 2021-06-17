package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepo geologicalClassRepo;

    public List<Section> findAllSections() {
        return sectionRepository.findAll();
    }

    public List<GeologicalClass> findAllClasses() {
        return geologicalClassRepo.findAll();
    }

    public void createFile() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sections sheet");

        List<Section> sectionList = findAllSections().stream()
                .sorted(Comparator.comparing(Section::getName))
                .collect(toList());
        List<GeologicalClass> geologicalClassList = findAllClasses();

        List<Character> classNumbers = geologicalClassList.stream()
                .map(c -> c.getName().charAt(c.getName().length() - 1))
                .distinct()
                .sorted()
                .collect(toList());

        System.out.println(classNumbers);

        int rownum = 0;
        Cell cell;
        Row row;
        //

        row = sheet.createRow(rownum);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Section name");

        int k = 0;
        for (Character className : classNumbers) {
//            String name = geologicalClass.getName().replaceAll("[^\\d]", "").replaceFirst(".$","");
            cell = row.createCell(k + 1, CellType.STRING);
            cell.setCellValue("Class " + className + " name");

            cell = row.createCell(k + 2, CellType.STRING);
            cell.setCellValue("Class " + className + " code");
            k = k + 2;
        }


        for (Section section : sectionList) {
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(section.getName());

            List<String> codes = section.getCodes().stream()
                    .sorted()
                    .collect(toList());
            int p = 0;
            int i = 0;
            for (int j = 0; j < codes.size(); j++) {
                GeologicalClass geologicalClass = findClassByCode(codes.get(j));
                String name = geologicalClass.getName();

                System.out.println(name.charAt(name.length() - 1));
                System.out.println(classNumbers.get(i));
                if (name.charAt(name.length() - 1) == classNumbers.get(i)) {
                    cell = row.createCell(p + 1, CellType.STRING);
                    cell.setCellValue(name);

                    cell = row.createCell(p + 2, CellType.STRING);
                    cell.setCellValue(codes.get(j));

                }  else {
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
                .map(GeologicalClass::getCode)
                .collect(toList());
        String name = sectionRequest.getName();
        Section section = sectionRepository.findById(name).orElse(new Section());
        if (sectionRepository.findById(name).isPresent()) {
            List<String> codes = section.getCodes();
            codes.addAll(classCodes);
        } else {
            section.setName(name);
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

    public List<String> findSectionsByCode(String code) {
        return sectionRepository.findSectionsByCode(code);
    }

    public GeologicalClass findClassByCode(String code) {
        return geologicalClassRepo.findByCode(code);
    }

}
