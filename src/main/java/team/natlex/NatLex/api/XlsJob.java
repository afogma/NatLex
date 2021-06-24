package team.natlex.NatLex.api;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class XlsJob {

    private UUID id;
    private byte[] content;
    private JobStatus status;

    public XlsJob(byte[] content) {
        this.id = randomUUID();
        this.content = content;
        this.status = IN_PROGRESS;
    }

    @RequiredArgsConstructor
    @Getter
    public static enum JobStatus {

        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        private final String code;

    }

}
