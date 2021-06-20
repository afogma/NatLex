package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;
    private final FileService fileService;

    @GetMapping
    @RequestMapping("/sections")
    public List<SectionFullDTO> showSectionList() {
        List<SectionFullDTO> section = apiService.findAllSections();
        return section;
    }

    @PostMapping
    public ResponseEntity addNewSection(@RequestBody SectionFullDTO sectionFullDTO) {
        apiService.addNewSection(sectionFullDTO);
        System.out.println(sectionFullDTO);
        return ResponseEntity.ok("section added");
    }

    @GetMapping
    @RequestMapping("/export")
    public ResponseEntity createFile() {
        fileService.createFile();
        return ResponseEntity.ok("file created");
    }

    @GetMapping
    @RequestMapping("/import")
    public ResponseEntity readFile() throws IOException {
        fileService.readFile();
        return ResponseEntity.ok("file loaded");
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
    public ResponseEntity<String> updateSection(@RequestBody SectionFullDTO sectionFullDTO, @PathVariable String name) {
        apiService.updateSection(sectionFullDTO, name);
        return ResponseEntity.ok("section updated");
    }

    @DeleteMapping
    @RequestMapping("/section/delete/{name}")
    public ResponseEntity deleteSection(@PathVariable String name) {
        apiService.deleteSection(name);
        return ResponseEntity.ok("section " + name + " deleted");
    }
}
