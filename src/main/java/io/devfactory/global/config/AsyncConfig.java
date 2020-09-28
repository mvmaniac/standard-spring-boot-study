package io.devfactory.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    final int processors = Runtime.getRuntime().availableProcessors();
    log.debug("[dev] processors count: {}", processors);

    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(processors);
    executor.setMaxPoolSize(processors * 2);
    executor.setQueueCapacity(50);
    executor.setKeepAliveSeconds(60);
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.initialize();

    return executor;
  }

}
