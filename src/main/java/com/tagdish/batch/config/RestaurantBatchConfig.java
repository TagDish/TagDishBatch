package com.tagdish.batch.config;

import javax.sql.DataSource;

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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tagdish.batch.itemprocessor.RestaurantItemProcessor;
import com.tagdish.batch.itemwriter.RestaurantItemWriter;
import com.tagdish.dao.jdbc.preparedstatementsetter.RestaurantPreparedStatementSetter;
import com.tagdish.dao.jdbc.rowmapper.AccountRowMapper;
import com.tagdish.domain.db.AccountDB;
import com.tagdish.domain.elasticsearch.Restaurant;

@Configuration
@EnableBatchProcessing
public class RestaurantBatchConfig {
	
	@Autowired
	private DataSource dataSource;
		
	@Value("${restaurant.fetch.size}")
	private int restaurantFetchSize;
	
	@Value("${restaurant.chunk.size}")
	private int restaurantChunkSize;	

    @Bean
    public ItemReader<AccountDB> restaurantItemReader() {
    	JdbcCursorItemReader<AccountDB> restaurantItemReader = new JdbcCursorItemReader<AccountDB>();
    	
    	restaurantItemReader.setSql("Select * from Account, Geotarget where Geotarget.adGroup_id = Account.id "
    			+ "	and ("
    			+ "		(account.createdDate is not null and account.updatedDate is null and account.createdDate > (CURDATE()-1)) or "
    			+ "     (account.updatedDate is not null and account.updatedDate > (CURDATE()-1)) or "
    			+ "     (Geotarget.createdDate is not null and Geotarget.updatedDate is null and Geotarget.createdDate > (CURDATE()-1)) or "
    			+ "     (Geotarget.updatedDate is not null and Geotarget.updatedDate > (CURDATE()-1))"
    			+ " ) ");
    	restaurantItemReader.setDataSource(dataSource);
    	restaurantItemReader.setFetchSize(restaurantFetchSize);
//    	restaurantItemReader.setPreparedStatementSetter(new RestaurantPreparedStatementSetter());
    	restaurantItemReader.setRowMapper(new AccountRowMapper(true));

        return restaurantItemReader;
    }
    
   
    @Bean
    public ItemProcessor<AccountDB, Restaurant> restaurantItemProcessor() {
        return new RestaurantItemProcessor();
    }
    
    @Bean
    public ItemWriter<Restaurant> restaurantItemWriter() {
    	RestaurantItemWriter restaurantItemWriter = new RestaurantItemWriter();

        return restaurantItemWriter;
    }
    
    @Bean
    public Job importRestaurantJob(JobBuilderFactory jobs, Step stepRestaurant
    		, JobExecutionListener listener) {
        return jobs.get("importRestaurantJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepRestaurant)
                .end()
                .build();	
    }

    @Bean
    public Step stepRestaurant(StepBuilderFactory stepBuilderFactory, 
            ItemWriter<Restaurant> restaurantItemWriter, ItemReader<AccountDB> restaurantItemReader, ItemProcessor<AccountDB, Restaurant> restaurantItemProcessor) {
        return stepBuilderFactory.get("stepRestaurant")
                .<AccountDB, Restaurant> chunk(restaurantChunkSize)
                .reader(restaurantItemReader)
                .processor(restaurantItemProcessor)
                .writer(restaurantItemWriter)
                .build();
    }    
}
