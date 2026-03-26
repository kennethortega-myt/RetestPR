package pe.gob.onpe.consultaopbackend.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Slf4j
public class AsynConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor asyncExecutor() {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        log.info("Virtual Thread Executor habilitado con Java 21");
        return executor;
    }
}
