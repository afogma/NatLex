package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.natlex.NatLex.service.ApiService;
import team.natlex.NatLex.service.XlsService;
import team.natlex.NatLex.db.GeologicalClass;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.model.XlsJob;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;
    private final XlsService xlsService;

    @GetMapping("/sections")
    public List<SectionFullDTO> showSectionList() {
        List<SectionFullDTO> section = apiService.findAllSections();
        return section;
    }

    @GetMapping("/sections/by-code")
    public List<String> showAllSectionsByClassCode(@RequestParam String code) {
        return apiService.findSectionsByCode(code);
    }

    @GetMapping("/classes/{code}")
    public GeologicalClass showAllClassesByCode(@PathVariable String code) {
        return apiService.findClassByCode(code);
    }

    @PostMapping("/section/add")
    public void addNewSection(@RequestBody SectionFullDTO sectionFullDTO) {
        apiService.addNewSection(sectionFullDTO);
    }

    @PutMapping("/section/{name}")
    public void updateSection(@RequestBody SectionFullDTO sectionFullDTO, @PathVariable String name) {
        apiService.updateSection(sectionFullDTO, name);
    }

    @DeleteMapping("/section/{name}")
    public void deleteSection(@PathVariable String name) {
        apiService.deleteSection(name);
    }

    @PostMapping("/class/add")
    public void addNewClass(@RequestBody GeologicalClass geoClass) {
        apiService.addNewClass(geoClass);
    }

    @PutMapping("/class/{name}")
    public void updateClass(@RequestBody GeologicalClass geoClass, @PathVariable String name) {
        apiService.updateClass(geoClass, name);
    }

    @DeleteMapping("/class/{name}")
    public void deleteClass(@PathVariable String name) {
        apiService.deleteClass(name);
    }

    @PostMapping("/import")
    public UUID uploadFile(@RequestParam("file") MultipartFile file) {
        XlsJob job = xlsService.loadXls(file);
        return job.getId();
    }

    @GetMapping("/export")
    public UUID downloadFile() {
        XlsJob job = xlsService.exportXls();
        return job.getId();
    }

    @GetMapping("/export/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id) {
        byte[] content = xlsService.downloadFile(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentLength(content.length);
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"job_" + id + ".xls\"");
        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<XlsJob.JobStatus> getExportStatus(@PathVariable UUID id) {
        XlsJob.JobStatus status = xlsService.getJobStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/import/{id}")
    public ResponseEntity<XlsJob.JobStatus> getImportStatus(@PathVariable UUID id) {
        XlsJob.JobStatus status = xlsService.getJobStatus(id);
        return ResponseEntity.ok(status);
    }
}
