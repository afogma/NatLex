package team.natlex.NatLex.api;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@Data
public class XlsJobExecutor {

    private UUID id;
    private byte[] file;
    private JobStatus status;


    @RequiredArgsConstructor
    @Getter
    private static enum JobStatus {

        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        private final String code;

    }

}
