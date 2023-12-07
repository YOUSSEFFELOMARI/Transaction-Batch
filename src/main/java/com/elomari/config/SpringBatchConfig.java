package com.elomari.config;

import com.elomari.batch.CustomItemPrecessorBuisness;
import com.elomari.batch.CustomItemProcessor;
import com.elomari.batch.RecordFieldSetMapper;
import com.elomari.batch.StepSkipListener;
import com.elomari.dto.TransactionDto;
import com.elomari.entity.Transaction;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Configuration
@EnableScheduling
//@EnableBatchProcessing
//@RequiredArgsConstructor
public class SpringBatchConfig {

    @Value("${csvFile}")
    private PathResource inputCsv;
    @Value("${xmlFile}")
    private PathResource inputXml;


    @Bean("CsvReader")
    public ItemReader<TransactionDto> csvItemReader(){
        FlatFileItemReader<TransactionDto> reader=new FlatFileItemReader<>();
        reader.setResource(inputCsv);
        DelimitedLineTokenizer tokenizer=new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        tokenizer.setNames("transactionId","compteId","montant","transactionDate");
        DefaultLineMapper<TransactionDto> lineMapper=new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean("XmlReader")
    public ItemReader<TransactionDto> xmlItemReader(){
        StaxEventItemReader<TransactionDto> reader=new StaxEventItemReader<>();
        reader.setResource(inputXml);
        reader.setFragmentRootElementName("transaction");
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAnnotatedClasses(TransactionDto.class);
        marshaller.getXStream().addPermission(NoTypePermission.NONE);
        marshaller.getXStream().addPermission(NullPermission.NULL);
        marshaller.getXStream().addPermission(PrimitiveTypePermission.PRIMITIVES);
        marshaller.getXStream().allowTypes(new Class[] {TransactionDto.class});
        reader.setUnmarshaller(marshaller);
        return reader;
    }


    @Bean
    public ItemProcessor itemProcessor() {
        CompositeItemProcessor compositeItemProcessor=new CompositeItemProcessor<>();
        List<ItemProcessor> delegates= new ArrayList<>();
        delegates.add(getFrist());
        delegates.add(getSecond());
        compositeItemProcessor.setDelegates(delegates);
        return compositeItemProcessor;
    }

    @Bean
    public ItemProcessor getFrist(){
        return new CustomItemProcessor();
    }

    @Bean
    public ItemProcessor getSecond(){
        return new CustomItemPrecessorBuisness();
    }

    @Bean
    public ItemWriter<Transaction> itemWriter() {
        CompositeItemWriter<Transaction> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<Transaction>> delegates = new ArrayList<>();
        delegates.add(transactionInsertWriter());
        delegates.add(compteUpdateWriter());
        compositeItemWriter.setDelegates(Collections.unmodifiableList(delegates));
        return compositeItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> transactionInsertWriter() {
        JdbcBatchItemWriter<Transaction> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setAssertUpdates(true);
        jdbcBatchItemWriter.setDataSource(dataSource());
        jdbcBatchItemWriter.setSql(
                "INSERT INTO transaction (transaction_id, debit_date, montant, transaction_date, compte_id) " +
                        "VALUES (:transactionId, :debitDate, :montant, :transactionDate, :compte.compteId);"
        );
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return jdbcBatchItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> compteUpdateWriter() {
        JdbcBatchItemWriter<Transaction> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setAssertUpdates(true);
        jdbcBatchItemWriter.setDataSource(dataSource());
        jdbcBatchItemWriter.setSql(
                "UPDATE compte " +
                        "SET solde = :compte.solde " +
                        "WHERE compte_id = :compte.compteId;"
        );
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return jdbcBatchItemWriter;
    }
    @Bean
    public Flow xmlFlow() throws Exception {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(xmlStep())
                .build();
    }
    @Bean
    public Flow csvFlow() throws Exception {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(csvStep())
                .build();
    }


    @Bean(name = "csvStep")
    protected Step csvStep() throws Exception {
        return new StepBuilder("csvStep", JobRepository())
                .<TransactionDto, Transaction>chunk(10, TransactionManager())
                .reader(csvItemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(new StepSkipListener())
                .build();
    }

    @Bean(name = "xmlStep")
    protected Step xmlStep() throws Exception {
        return new StepBuilder("xmlStep", JobRepository())
                .<TransactionDto, Transaction>chunk(10, TransactionManager())
                .reader(xmlItemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(new StepSkipListener())
                .build();
    }

    @Bean(name = "firstBatchJob")
    public Job job() throws Exception {
        return new JobBuilder("firstBatchJob", JobRepository())
                .start(csvFlow())
                .split(new SimpleAsyncTaskExecutor())
                .add(xmlFlow())
                .end()
                .build();
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/transaction_batch")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username("root")
                .password("root")
                .build();
    }
    /**The ResourcelessTransactionManager is a class in the Spring Framework that provides a simple implementation of the PlatformTransactionManager interface. This implementation is intended for scenarios where no actual transaction management is needed, particularly in environments where transactions are not supported or unnecessary.
     * <p>1- Transaction Management for Testing or Lightweight Scenarios: It is commonly used in testing environments or in scenarios where a lightweight transaction manager is sufficient. For example, when testing components that interact with a database, but you don't need to commit or roll back transactions.</p>
     * <p>2- No External Resources Involved: The name "Resourceless" indicates that this transaction manager does not involve external resources like databases or message queues. It is not meant for managing distributed transactions.</p>
     * <p>3- In-Memory Transaction Management: Instead of interacting with an actual transactional resource, ResourcelessTransactionManager manages transactions in-memory. It doesn't have the complexity of dealing with two-phase commit protocols or distributed transactions.</p>
     * <p>** DataSourceTransactionManager if a DataSource is provided within the context</p>
     * <p>** ResourcelessTransactionManager if no DataSource is provided within the context</p>*/
    @Bean(name = "transactionManager")
    public PlatformTransactionManager TransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean(name = "jobRepository")
    public JobRepository JobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(TransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "jobLauncher")
    public JobLauncher JobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(JobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
//   @Scheduled(cron="0 0 0 1 1/1 *")//every first day of every month
//    @Scheduled(cron = "0 * * * * *")//Every minute
    public void launchBatch()
            throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("date", new Date());
        JobExecution execution = JobLauncher().run(job(), builder.toJobParameters());
        System.out.println("Job Status : " + execution.getStatus());
        System.out.println("Job Status : ");
    }
}
