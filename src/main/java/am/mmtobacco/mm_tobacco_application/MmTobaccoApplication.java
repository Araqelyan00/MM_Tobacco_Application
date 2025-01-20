package am.mmtobacco.mm_tobacco_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MmTobaccoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MmTobaccoApplication.class, args);
    }
}

