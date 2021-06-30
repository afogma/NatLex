package team.natlex.NatLex.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class XlsServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    XlsService xlsService = new XlsService(sectionRepo, geologicalClassRepo);

    @Test
    void xlsExportProcess() {
    }

    @Test
    void loadFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", Files.readAllBytes(Paths.get("sections.xls")));
        var job = new XlsJob(multipartFile.getBytes());
        xlsService.loadFile(job);
        assertEquals(XlsJob.JobStatus.DONE, job.getStatus());

    }

    @Test
    void loadXls() {


    }

    @Test
    void exportXls() {
    }

    @Test
    void downloadFile() {
    }

    @Test
    void getJobStatus() {
    }
}