package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")
public class ApiController {

    private final ApiService apiService;
    private final XlsService xlsService;

    @GetMapping
    @RequestMapping("/sections")
    @PreAuthorize("permitAll")
    public List<SectionFullDTO> showSectionList() {
        List<SectionFullDTO> section = apiService.findAllSections();
        return section;
    }

    @GetMapping
    @RequestMapping("/sections/by-code")
    @PreAuthorize("permitAll")
    public List<String> showAllSectionsByClassCode(@RequestParam String code) {
        return apiService.findSectionsByCode(code);
    }

    @GetMapping
    @RequestMapping("/classes/{code}")
    @PreAuthorize("permitAll")
    public GeologicalClass showAllClassesByCode(@PathVariable String code) {
        return apiService.findClassByCode(code);
    }

    @PutMapping
    @RequestMapping("/section/update/{name}")
    public ResponseEntity<String> updateSection(@RequestBody SectionFullDTO sectionFullDTO, @PathVariable String name) {
        apiService.updateSection(sectionFullDTO, name);
        return ResponseEntity.ok("section updated");
    }

    @DeleteMapping
    @RequestMapping("/section/delete/{name}")
    public ResponseEntity<String> deleteSection(@PathVariable String name) {
        apiService.deleteSection(name);
        return ResponseEntity.ok(name + " deleted");
    }

    @PostMapping
    @RequestMapping("/section/add")
    public ResponseEntity<String> addNewSection(@RequestBody SectionFullDTO sectionFullDTO) {
        apiService.addNewSection(sectionFullDTO);
        System.out.println(sectionFullDTO);
        return ResponseEntity.ok("section(s) added");
    }

    @PostMapping
    @RequestMapping("/class/add")
    public ResponseEntity<String> addNewClass(@RequestBody GeologicalClass geoClass) {
        apiService.addNewClass(geoClass);
        return ResponseEntity.ok("class added");
    }

    @PutMapping
    @RequestMapping("/class/update/{name}")
    public ResponseEntity<String> updateClass(@RequestBody GeologicalClass geoClass, @PathVariable String name) {
        apiService.updateClass(geoClass, name);
        return ResponseEntity.ok("class updated");
    }

    @DeleteMapping
    @RequestMapping("/class/delete/{name}")
    public ResponseEntity<String> deleteClass(@PathVariable String name) {
        apiService.deleteClass(name);
        return ResponseEntity.ok(name + " removed");
    }

    @PostMapping
    @RequestMapping("/import")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        XlsJob job = xlsService.loadXls(file);
        return ResponseEntity.ok("file uploaded, id: " + job.getId());
    }

    @GetMapping
    @RequestMapping("/export")
    public ResponseEntity<String> downloadFile() throws IOException {
        XlsJob job = xlsService.exportXls();
        return ResponseEntity.ok("file processing. id: " + job.getId());
    }

    @GetMapping
    @RequestMapping("/export/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id) {
        byte[] content = xlsService.downloadFile(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentLength(content.length);
        responseHeaders.set("Content-disposition","attachment; filename=\""+  "job_" + id + ".xls");
        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }
}
