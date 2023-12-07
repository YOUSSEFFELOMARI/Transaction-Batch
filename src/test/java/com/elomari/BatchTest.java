package com.elomari;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
public class BatchTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void testBatchJob() throws Exception {
        // Define the input file resource for the test
        ClassPathResource inputCsv = new ClassPathResource("test-data.csv");

        // Create Job Parameters with the input file path
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputCsv", inputCsv.getFile().getAbsolutePath())
                .toJobParameters();

        // Launch the job and capture the JobExecution
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        // Assert that the job completed successfully
        assert(jobExecution.getStatus() == BatchStatus.COMPLETED);
    }
}
