package com.tagdish.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.tagdish.batch.domain.BatchZipCode;
import com.tagdish.batch.itemprocessor.ZipCodeItemProcessor;
import com.tagdish.batch.itemwriter.ZipCodeItemWriter;
import com.tagdish.domain.elasticsearch.ZipCode;

@Configuration
@EnableBatchProcessing
public class ZipCodeBatchConfig {

	@Value("${zipcode.chunk.size}")
	private int zipcodeChunkSize;		
	
    @Bean
    public ItemReader<BatchZipCode> reader() {
        FlatFileItemReader<BatchZipCode> reader = new FlatFileItemReader<BatchZipCode>();
        
        reader.setResource(new ClassPathResource("ZIP_CODES.txt"));
        reader.setLineMapper(new DefaultLineMapper<BatchZipCode>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "zipCode", "latitude", "longtitude", "city", "state", "county", "zipClass" });
                setDelimiter(DELIMITER_COMMA);
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<BatchZipCode>() {{
                setTargetType(BatchZipCode.class);
            }});
        }});
        return reader;
    }
    
    @Bean
    public ItemProcessor<BatchZipCode, ZipCode> processor() {
        return new ZipCodeItemProcessor();
    }
    
    @Bean
    public ItemWriter<ZipCode> writer() {
        ZipCodeItemWriter writer = new ZipCodeItemWriter();

        return writer;
    }
    
    @Bean
    public Job importZipCodeJob(JobBuilderFactory jobs, Step step1
    		, JobExecutionListener listener
    		) {
        return jobs.get("importZipCodeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();	
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<BatchZipCode> reader,
            ItemWriter<ZipCode> writer, ItemProcessor<BatchZipCode, ZipCode> processor) {
        return stepBuilderFactory.get("step1")
                .<BatchZipCode, ZipCode> chunk(zipcodeChunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }    
}
