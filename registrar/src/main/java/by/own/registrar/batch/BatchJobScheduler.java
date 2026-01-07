package by.own.registrar.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobScheduler {

    private final JobOperator jobOperator;
    private final Job registerEventJob;

    @Scheduled(fixedDelayString = "${app.batch.job-interval-ms}")
    public void runJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

            JobExecution start = jobOperator.start(registerEventJob, params);
            log.info("Пакетная обработка успешно запущена с executionId={}", start.getId());
        } catch (Exception e) {
            log.error("Ошибка при запуске пакетной обработки", e);
        }
    }
}