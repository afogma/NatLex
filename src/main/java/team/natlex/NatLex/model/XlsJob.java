package team.natlex.NatLex.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static team.natlex.NatLex.model.XlsJob.JobStatus.IN_PROGRESS;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class XlsJob {

    private UUID id = randomUUID();
    private byte[] content;
    private JobStatus status = IN_PROGRESS;

    public XlsJob(byte[] content) {
        this.content = content;
    }

    @RequiredArgsConstructor
    @Getter
    public enum JobStatus {

        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        private final String code;

    }
}
