package by.own.generator2.config;

import by.own.generator2.properties.EventGeneratorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(EventGeneratorProperties.class)
@Configuration
public class EventGeneratorConfiguration {}