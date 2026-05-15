package com.example.demo.config;

import java.util.concurrent.Executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.demo.summarization.SummarizationProperties;

@Configuration
@EnableAsync
@EnableConfigurationProperties(SummarizationProperties.class)
public class AsyncSummarizationConfig {

  @Bean(name = "summarizationExecutor")
  public Executor summarizationExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("summarize-");
    executor.initialize();
    return executor;
  }
}
