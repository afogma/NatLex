package team.natlex.NatLex.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
class ApiService {

    private final ApiRepository apiRepository;

    public List<Section> findAll () {
        return apiRepository.findAll().stream()
                .sorted(Comparator.comparing(Section::getName))
                .collect(toList());
    }


}
