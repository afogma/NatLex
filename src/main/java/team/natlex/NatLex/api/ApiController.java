package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;
    private final XlsService xlsService;

    @GetMapping
    @RequestMapping("/sections")
    public List<SectionFullDTO> showSectionList() {
        List<SectionFullDTO> section = apiService.findAllSections();
        return section;
    }

    @PostMapping
    @RequestMapping("/section/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addNewSection(@RequestBody SectionFullDTO sectionFullDTO) {
        apiService.addNewSection(sectionFullDTO);
        System.out.println(sectionFullDTO);
        return ResponseEntity.ok("section(s) added");
    }

    @GetMapping
    @RequestMapping("/export")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity downloadFile() throws IOException {
        XlsJob job = xlsService.exportXls();
        return ResponseEntity.ok("file processing. id: " + job.getId());
    }

    @GetMapping
    @RequestMapping("/export/{id}/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id) {
        byte[] content = xlsService.downloadFile(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentLength(content.length);
        responseHeaders.set("Content-disposition","attachment; filename=\""+  "job_" + id + ".xls");

        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/import")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        XlsJob job = xlsService.loadXls(file);
        return ResponseEntity.ok("file loaded, id: " + job.getId());
    }

    @GetMapping
    @RequestMapping("/sections/by-code")
    public List<String> showAllSectionsByClassCode(@RequestParam String code) {
        return apiService.findSectionsByCode(code);
    }

    @GetMapping
    @RequestMapping("/classes/{code}")
    public GeologicalClass showAllClassesByCode(@PathVariable String code) {
        return apiService.findClassByCode(code);
    }

    @PutMapping
    @RequestMapping("/section/update/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateSection(@RequestBody SectionFullDTO sectionFullDTO, @PathVariable String name) {
        apiService.updateSection(sectionFullDTO, name);
        return ResponseEntity.ok("section updated");
    }

    @DeleteMapping
    @RequestMapping("/section/delete/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteSection(@PathVariable String name) {
        apiService.deleteSection(name);
        return ResponseEntity.ok(name + " deleted");
    }
}
