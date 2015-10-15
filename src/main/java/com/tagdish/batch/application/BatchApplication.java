package com.tagdish.batch.application;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tagdish.dao.repository.DishRepository;

public class BatchApplication {

    public static void main(String[] args) throws Exception {
    	
    	if(args.length != 1) {
    		System.out.println("Improper number of agruments!!");
    		System.out.println("Please use give the job name and format of the batch start 'BatchApplication <batchname>'");
    		System.out.println("Sample example 'BatchApplication importDishJob'");
    		System.exit(0);
    	}
    	
    	if(!args[0].equals("importDishJob")) {
    		System.out.println("Invalid job name!! Valid job name are importDishJob/importZipCodeJob");
    		System.out.println("Please use give the job name and format of the batch start 'BatchApplication <batchname>'");
    		System.out.println("Sample example 'BatchApplication importDishJob'");
    		System.exit(0);
    	}
    	    	
    	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
    	
    	JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    	Job importJob = applicationContext.getBean(args[0], Job.class);
    	
    	JobParameters jobParameters = new JobParametersBuilder()
		.addDate("date", new Date())
		.toJobParameters();
    	
    	JobExecution jobExecution = jobLauncher.run(importJob, jobParameters);
    	
    	BatchStatus batchStatus = jobExecution.getStatus();
    	while(batchStatus.isRunning()){
    		System.out.println("*********** Still running.... **************");
    		Thread.sleep(1000);
    	}
    	
    	if(jobExecution.getAllFailureExceptions() != null && jobExecution.getAllFailureExceptions().size() > 0) {
    		
    		for (Throwable th : jobExecution.getAllFailureExceptions()) {
				System.out.println(th);
			}
    	}
    	
    	System.out.println(String.format("*********** Exit status: %s", jobExecution.getExitStatus().getExitDescription()));
    	System.out.println(String.format("*********** Exit status: %s", jobExecution.getExitStatus().getExitCode()));
    	JobInstance jobInstance = jobExecution.getJobInstance();
    	System.out.println(String.format("********* Name of the job %s", jobInstance.getJobName()));
    	
    	System.out.println(String.format("*********** job instance Id: %d", jobInstance.getId()));
    	
//		Test code    	
    	System.out.println(applicationContext.getBean(DishRepository.class).findByDishId(1l).getDishName());
//    	System.out.println(applicationContext.getBean(ZipCodeRepository.class).findByCityAndState("Torrance", "CA").size());
    }
}