package team.natlex.NatLex.api;

import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class XlsJob {

    private UUID id;
    private byte[] file;
    private JobStatus status;

    public XlsJob(byte[] file) {
        this.file = file;
    }

    @RequiredArgsConstructor
    @Getter
    private static enum JobStatus {

        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        private final String code;

    }

}
