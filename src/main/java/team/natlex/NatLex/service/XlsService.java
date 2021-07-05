package team.natlex.NatLex.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.natlex.NatLex.model.SectionFullDTO;
import team.natlex.NatLex.model.XlsJob;
import team.natlex.NatLex.db.GeologicalClassRepo;
import team.natlex.NatLex.db.SectionRepo;
import team.natlex.NatLex.exceptions.ExportStillInProgressException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;
import static team.natlex.NatLex.model.XlsJob.JobStatus.ERROR;
import static team.natlex.NatLex.model.XlsJob.JobStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class XlsService {

    Logger logger = LoggerFactory.getLogger(XlsService.class);

    private final SectionRepo sectionRepo;
    private final GeologicalClassRepo geologicalClassRepo;
    private final XlsAdapter xlsAdapter;
    private final ApiService apiService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Map<UUID, XlsJob> jobs = new ConcurrentHashMap<>();

    public void xlsExportProcess(XlsJob job) {
        try {
            var sectionFullDTOList = apiService.findAllSections();
            byte[] content = xlsAdapter.xlsExportProcess(sectionFullDTOList);
            job.setContent(content);
            job.setStatus(XlsJob.JobStatus.DONE);
            logger.info("job {} finished export", job.getId());
        } catch (Exception e) {
            logger.error("{} job failed", job.getId(), e);
        }
    }

    public void loadFile(XlsJob job) {
        try {
            var sectionDTOs = xlsAdapter.parseXls(job.getContent());
            var sections = sectionDTOs.stream()
                    .map(SectionFullDTO::sectionData)
                    .collect(toList());
            sectionRepo.saveAll(sections);
            var geoClassList = sectionDTOs.stream()
                    .flatMap(s -> s.getGeologicalClasses().stream())
                    .distinct()
                    .collect(toList());
            geologicalClassRepo.saveAll(geoClassList);
            job.setStatus(XlsJob.JobStatus.DONE);
            logger.info("job {} finished import", job.getId());
        } catch (Exception e) {
            job.setStatus(ERROR);
            logger.error("job {} failed to import", job.getId(), e);
        }
    }

    public XlsJob loadXls(MultipartFile file) {
        var job = new XlsJob(bytes(file));
        jobs.put(job.getId(), job);
        executorService.submit(() -> loadFile(job));
        return job;
    }

    private byte[] bytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public XlsJob exportXls() {
        var job = new XlsJob(null);
        jobs.put(job.getId(), job);
        executorService.submit(() -> xlsExportProcess(job));
        return job;
    }

    public byte[] downloadFile(UUID id) {
        if (jobs.get(id).getStatus() == IN_PROGRESS) throw new ExportStillInProgressException();
        return jobs.get(id).getContent();
    }

    public XlsJob.JobStatus getJobStatus(UUID id) {
        return jobs.get(id).getStatus();
    }
}
