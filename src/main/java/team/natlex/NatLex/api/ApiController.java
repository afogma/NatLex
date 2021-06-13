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

    @PostMapping
    @RequestMapping("/export")
    public ResponseEntity createFile() {
        apiService.createFile();
        return ResponseEntity.ok("file created");
    }

//    @GetMapping
//    @RequestMapping("/classList")
//    public List<Section> showAllGeoClassesBySection(@PathVariable String name) {
//        return apiService.findAllClassesBySections(name);
//    }

    @GetMapping
    @RequestMapping("/sections/{classCode}")
    public List<String> showAllSectionsByClassCode(@PathVariable String classCode) {
        return apiService.findSectionsByCode(classCode);
    }
}
