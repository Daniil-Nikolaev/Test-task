package by.own.registrar.config;

import by.own.registrar.model.RegisteredEvent;
import by.own.sharedsources.dto.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.kafka.KafkaItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaItemReader<String, EventCreatedMessage> kafkaItemReader;
    private final ItemProcessor<EventCreatedMessage, RegisteredEvent> eventProcessor;
    private final ItemWriter<RegisteredEvent> eventWriter;

    @Value("${app.batch.chunk-size}")
    private int chunkSize;

    @Bean
    public Step registerEventStep() {
        return new StepBuilder("registerEventStep", jobRepository)
            .<EventCreatedMessage, RegisteredEvent>chunk(chunkSize)
            .transactionManager(transactionManager)
            .reader(kafkaItemReader)
            .processor(eventProcessor)
            .writer(eventWriter)
            .faultTolerant()
            .skip(Exception.class)
            .skipLimit(30)
            .build();
    }

    @Bean
    public Job registerEventJob() {
        return new JobBuilder("registerEventJob", jobRepository)
            .start(registerEventStep())
            .build();
    }
}