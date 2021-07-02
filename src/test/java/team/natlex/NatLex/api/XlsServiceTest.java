package team.natlex.NatLex.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import team.natlex.NatLex.api.entity.GeologicalClass;
import team.natlex.NatLex.api.entity.Section;
import team.natlex.NatLex.api.model.XlsJob;
import team.natlex.NatLex.api.repository.GeologicalClassRepo;
import team.natlex.NatLex.api.repository.SectionRepo;
import team.natlex.NatLex.api.service.XlsService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XlsServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    XlsService xlsService = new XlsService(sectionRepo, geologicalClassRepo);

    @Test
    void xlsExportProcess() {
    }

    @Test
    void loadFile() throws IOException {
        var multipartFile = new MockMultipartFile("file", Files.readAllBytes(Paths.get("sections.xls")));
        var job = new XlsJob(multipartFile.getBytes());
        xlsService.loadFile(job);
        assertEquals(XlsJob.JobStatus.DONE, job.getStatus());
        verify(sectionRepo).save(new Section("Section 1", asList("GC11", "GC12", "GC13")));
        verify(sectionRepo).save(new Section("Section 2", asList("GC21", "GC23")));
        verify(sectionRepo).save(new Section("Section 3", asList("GC31", "GC32")));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 11","GC11"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 12","GC12"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 13","GC13"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 21","GC21"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 23","GC23"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 31","GC31"));
        verify(geologicalClassRepo).save(new GeologicalClass("Geo Class 32","GC32"));
    }

    @Test
    void loadXls() throws IOException {
        var multipartFile = new MockMultipartFile("file", Files.readAllBytes(Paths.get("sections.xls")));
        var job = xlsService.loadXls(multipartFile);
        assertEquals(XlsJob.JobStatus.IN_PROGRESS, job.getStatus());
    }

    @Test
    void exportXls() {
        var job = xlsService.exportXls();
        assertEquals(XlsJob.JobStatus.IN_PROGRESS, job.getStatus());
    }

    @Test
    void downloadFile() throws IOException {
        var job = xlsService.exportXls();
        job.setContent(Files.readAllBytes(Paths.get("sections.xls")));
        System.out.println(job.getContent().length);
        xlsService.xlsExportProcess(job);
        var content = xlsService.downloadFile(job.getId());
        var byteArrayInputStream = new ByteArrayInputStream(content);
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
            verify(sectionRepo).save(section);
            for (GeologicalClass gc : geologicalClassList) {
                verify(geologicalClassRepo).save(gc);
            }
        }
    }

    @Test
    void getJobStatus() {
        var job = xlsService.exportXls();
        UUID id = job.getId();
        XlsJob.JobStatus status = xlsService.getJobStatus(id);
        assertEquals(status, XlsJob.JobStatus.IN_PROGRESS);
        xlsService.xlsExportProcess(job);
        XlsJob.JobStatus newStatus = xlsService.getJobStatus(id);
        assertEquals(newStatus, XlsJob.JobStatus.DONE);
    }
}