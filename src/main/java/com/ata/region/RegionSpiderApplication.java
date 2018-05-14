package com.ata.region;

import com.ata.region.spider.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableRetry
public class RegionSpiderApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegionSpiderApplication.class, args);
    }
}
