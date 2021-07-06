package team.natlex.NatLex.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.db.Section;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.model.XlsJob;
import team.natlex.NatLex.db.GeologicalClassRepo;
import team.natlex.NatLex.db.SectionRepo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static team.natlex.NatLex.model.XlsJob.JobStatus.DONE;
import static team.natlex.NatLex.model.XlsJob.JobStatus.IN_PROGRESS;

class XlsServiceTest {

    SectionRepo sectionRepo = mock(SectionRepo.class);
    GeologicalClassRepo geologicalClassRepo = mock(GeologicalClassRepo.class);
    XlsAdapter xlsAdapter = mock(XlsAdapter.class);
    ApiService apiService = mock(ApiService.class);
    XlsService xlsService = new XlsService(sectionRepo, geologicalClassRepo, xlsAdapter, apiService);

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
    void downloadFile() throws Exception {
        var job = xlsService.exportXls();
        job.setContent("sections.xls".getBytes());
        xlsService.xlsExportProcess(job);
        var content = xlsService.downloadFile(job.getId());
        assertEquals(content, job.getContent());
    }

    @Test
    void getJobStatus() {
        var job = xlsService.exportXls();
        var id = job.getId();
        XlsJob.JobStatus status = xlsService.getJobStatus(id);
        assertEquals(status, XlsJob.JobStatus.IN_PROGRESS);
        xlsService.xlsExportProcess(job);
        XlsJob.JobStatus newStatus = xlsService.getJobStatus(id);
        assertEquals(newStatus, DONE);
    }

    @Test
    void xlsExportProcess() throws Exception {
        var multipartFile = new MockMultipartFile("file", "sections.xls".getBytes());
        var job = new XlsJob(multipartFile.getBytes());
        assertEquals(IN_PROGRESS, job.getStatus());
        xlsService.xlsExportProcess(job);
        assertEquals(DONE, job.getStatus());
    }

    @Test
    void loadFile() throws Exception {
        var multipartFile = new MockMultipartFile("file", "sections.xls".getBytes());
        var job = new XlsJob(multipartFile.getBytes());

        var sections = List.of(new Section("Section 1", List.of("GC11")));
        var geoClasses = List.of(new GeologicalClass("Geo Class 11", "GC11"));
        var sectionFullDTO = new SectionFullDTO("Section 1", List.of(new GeologicalClass("Geo Class 11", "GC11")));
        List<SectionFullDTO> sectionFullDTOList = List.of(sectionFullDTO);

        when(xlsAdapter.parseXls(job.getContent())).thenReturn(sectionFullDTOList);
        xlsService.loadFile(job);
        assertEquals(DONE, job.getStatus());

        verify(sectionRepo).saveAll(sections);
        verify(geologicalClassRepo).saveAll(geoClasses);
    }
}