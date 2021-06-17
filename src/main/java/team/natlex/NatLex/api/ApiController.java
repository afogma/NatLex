package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    @GetMapping
    public List<Section> showCabinetList() {
        return apiService.findAllSections();
    }

    @PostMapping
    public ResponseEntity addNewSection(@RequestBody SectionCreateRequest sectionCreateRequest) {
        apiService.addNewSection(sectionCreateRequest);
        System.out.println(sectionCreateRequest);
        return ResponseEntity.ok("section added");
    }

    @GetMapping
    @RequestMapping("/export")
    public ResponseEntity createFile() {
        apiService.createFile();
        return ResponseEntity.ok("file created");
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




}
