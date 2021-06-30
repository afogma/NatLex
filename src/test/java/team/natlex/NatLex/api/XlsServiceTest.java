package team.natlex.NatLex.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        System.out.println(content.length);
        assertEquals(content, job.getContent());

    }

    @Test
    void getJobStatus() {

    }
}