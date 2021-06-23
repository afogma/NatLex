package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class XlsService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepo geologicalClassRepo;
    private final XlsJob xlsJob;


    @Bean("singleThreaded")
    public ExecutorService singleThreadedExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Autowired
    private ExecutorService executorService;

    private Map<UUID, XlsJob> jobs = new ConcurrentHashMap<>();

    public ByteArrayOutputStream xlsExportProcess() {
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet("Sections sheet");

        var sectionList = sectionRepository.findAll().stream()
                .sorted(Comparator.comparing(Section::getName))
                .collect(toList());
        var geologicalClassList = geologicalClassRepo.findAll();

        var classNumbers = geologicalClassList.stream()
                .map(c -> c.getName().charAt(c.getName().length() - 1))
                .distinct()
                .sorted()
                .collect(toList());

        drawHeader(classNumbers, sheet);
        drawSections(sectionList, sheet, classNumbers);

        ByteArrayOutputStream outFile = new ByteArrayOutputStream();
        try  {
            workbook.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outFile;
    }

    public void drawHeader(List<Character> classNumbers, HSSFSheet sheet) {
        Cell cell;
        Row row;
        row = sheet.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Section name");

        var s = 0;
        for (Character className : classNumbers) {
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
                var geologicalClass = geologicalClassRepo.findByCode(codes.get(j));
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

    public void loadFile(XlsJob job) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(job.getContent());
        HSSFWorkbook workbook = new HSSFWorkbook(byteArrayInputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
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
            var section = new Section(sectionName, codes);
            sectionRepository.save(section);
            for (GeologicalClass gc : geologicalClassList) {
                geologicalClassRepo.save(gc);
            }
        }
        job.setStatus(XlsJob.JobStatus.DONE);
    }

    public XlsJob loadXls(MultipartFile file) throws IOException {
        var id = randomUUID();
        var job = new XlsJob(id, file.getBytes(), XlsJob.JobStatus.IN_PROGRESS);
        jobs.put(id, job);
        executorService.submit(() -> {
            try {
                loadFile(job);
            } catch (IOException e) {
               job.setStatus(XlsJob.JobStatus.ERROR);
            }
        });
        return job;
    }

    public XlsJob exportXls() {
        var id = randomUUID();
        ByteArrayOutputStream output  = (ByteArrayOutputStream) executorService.submit((Runnable) this::xlsExportProcess);
        byte[] content = output.toByteArray();
        var job = new XlsJob(id, content, XlsJob.JobStatus.IN_PROGRESS);
        jobs.put(id, job);
        return job;
    }

    public byte[] downloadFile(UUID id) {
        return jobs.get(id).getContent();
    }
}
