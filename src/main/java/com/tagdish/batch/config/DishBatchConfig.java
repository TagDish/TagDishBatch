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

import com.tagdish.batch.itemprocessor.DishItemProcessor;
import com.tagdish.batch.itemwriter.DishItemWriter;
import com.tagdish.dao.jdbc.preparedstatementsetter.DishPreparedStatementSetter;
import com.tagdish.dao.jdbc.rowmapper.AccountRowMapper;
import com.tagdish.dao.jdbc.rowmapper.DishRowMapper;
import com.tagdish.domain.db.DishDB;
import com.tagdish.domain.elasticsearch.Dish;

@Configuration
@EnableBatchProcessing
public class DishBatchConfig {
	
	@Autowired
	private DataSource dataSource;
		
	@Value("${dish.fetch.size}")
	private int dishFetchSize;
	
	@Value("${dish.chunk.size}")
	private int dishChunkSize;	

    @Bean
    public ItemReader<DishDB> dishItemReader() {
    	JdbcCursorItemReader<DishDB> dishItemReader = new JdbcCursorItemReader<DishDB>();
    	
//    	dishItemReader.setSql("Select * from Dish");
    	dishItemReader.setSql("Select * from Dish, Menu, Account, GeoTarget where Geotarget.adGroup_id = Account.id and "
    			+ " Account.id = Menu.Account_id and  Dish.menu_id = Menu.id and "
    			+ " ( "
    			+ "		(account.createdDate is not null and account.updatedDate is null and account.createdDate > (CURDATE()-1)) or "
    			+ "     (account.updatedDate is not null and account.updatedDate > (CURDATE()-1)) or "
    			+ "     (Geotarget.createdDate is not null and Geotarget.updatedDate is null and Geotarget.createdDate > (CURDATE()-1)) or "
    			+ "     (Geotarget.updatedDate is not null and Geotarget.updatedDate > (CURDATE()-1)) or "
    			+ "		(Menu.createdDate is not null and Menu.updatedDate is null and Menu.createdDate > (CURDATE()-1)) or "
    			+ "     (Menu.updatedDate is not null and Menu.updatedDate > (CURDATE()-1)) or "
    			+ "     (Dish.createdDate is not null and Dish.updatedDate is null and Dish.createdDate > (CURDATE()-1)) or "
    			+ "     (Dish.updatedDate is not null and Dish.updatedDate > (CURDATE()-1)) "    			
    			+ " )");
    	dishItemReader.setDataSource(dataSource);
    	dishItemReader.setFetchSize(dishFetchSize);
//    	dishItemReader.setPreparedStatementSetter(new DishPreparedStatementSetter());
    	dishItemReader.setRowMapper(new DishRowMapper(true, true, new AccountRowMapper(true)));

        return dishItemReader;
    }
   
    @Bean
    public ItemProcessor<DishDB, Dish> dishItemProcessor() {
        return new DishItemProcessor();
    }
    
    @Bean
    public ItemWriter<Dish> dishItemWriter() {
    	DishItemWriter dishItemWriter = new DishItemWriter();

        return dishItemWriter;
    }
    
    @Bean
    public Job importDishJob(JobBuilderFactory jobs, Step stepDish
    		, JobExecutionListener listener) {
        return jobs.get("importDishJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepDish)
                .end()
                .build();	
    }

    @Bean
    public Step stepDish(StepBuilderFactory stepBuilderFactory, 
            ItemWriter<Dish> dishItemWriter, ItemReader<DishDB> dishItemReader, ItemProcessor<DishDB, Dish> dishItemProcessor) {
        return stepBuilderFactory.get("stepDish")
                .<DishDB, Dish> chunk(dishChunkSize)
                .reader(dishItemReader)
                .processor(dishItemProcessor)
                .writer(dishItemWriter)
                .build();
    }    
}
