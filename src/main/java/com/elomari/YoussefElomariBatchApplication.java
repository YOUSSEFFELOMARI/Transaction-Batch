package com.elomari;

import com.elomari.config.SpringBatchConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.elomari.entity")
@EnableJpaRepositories("com.elomari.repository")
public class YoussefElomariBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoussefElomariBatchApplication.class,args);
	}

}
