package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.natlex.NatLex.exceptions.ExportStillInProgressException;
import team.natlex.NatLex.exceptions.ImportErrorException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class XlsService {

    Logger logger = LoggerFactory.getLogger(XlsService.class);

    private final SectionRepo sectionRepo;
    private final GeologicalClassRepo geologicalClassRepo;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Map<UUID, XlsJob> jobs = new ConcurrentHashMap<>();

    public XlsService(SectionRepo sectionRepo, GeologicalClassRepo geologicalClassRepo, Map<UUID, XlsJob> jobs) {
        this.sectionRepo = sectionRepo;
        this.geologicalClassRepo = geologicalClassRepo;
        this.jobs = jobs;
    }

    public void xlsExportProcess(XlsJob job) {
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet("Sections sheet");

        var sectionList = sectionRepo.findAll().stream()
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

        var outFile = new ByteArrayOutputStream();
        try {
            workbook.write(outFile);
            workbook.close();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        job.setContent(outFile.toByteArray());
        job.setStatus(XlsJob.JobStatus.DONE);
        logger.info("job {} finished export", job.getId());
    }

    private void drawHeader(List<Character> classNumbers, HSSFSheet sheet) {
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

    private void drawSections(List<Section> sectionList, HSSFSheet sheet, List<Character> classNumbers) {
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

    void loadFile(XlsJob job) throws IOException {
        var byteArrayInputStream = new ByteArrayInputStream(job.getContent());
        var workbook = new HSSFWorkbook(byteArrayInputStream);
        var sheet = workbook.getSheetAt(0);

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
            sectionRepo.save(section);
            for (GeologicalClass gc : geologicalClassList) {
                geologicalClassRepo.save(gc);
            }
        }
        job.setStatus(XlsJob.JobStatus.DONE);
        logger.info("job {} finished import", job.getId());
    }

    public XlsJob loadXls(MultipartFile file) throws IOException {
        var job = new XlsJob(file.getBytes());
        jobs.put(job.getId(), job);
        executorService.submit(() -> {
            try {
                loadFile(job);
            } catch (IOException e) {
                job.setStatus(XlsJob.JobStatus.ERROR);
                throw new ImportErrorException();
            }
        });
        return job;
    }

    public XlsJob exportXls() {
        var job = new XlsJob(null);
        jobs.put(job.getId(), job);
        executorService.submit(() -> {
            xlsExportProcess(job);
        });
        return job;
    }

    public byte[] downloadFile(UUID id) {
        if (jobs.get(id).getStatus() == XlsJob.JobStatus.IN_PROGRESS) throw new ExportStillInProgressException();
        return jobs.get(id).getContent();
    }

    public XlsJob.JobStatus getJobStatus(UUID id) {
        return jobs.get(id).getStatus();
    }
}
