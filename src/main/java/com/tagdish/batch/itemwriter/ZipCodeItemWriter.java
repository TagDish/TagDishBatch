package com.tagdish.batch.itemwriter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.tagdish.dao.repository.ZipCodeRepository;
import com.tagdish.domain.elasticsearch.ZipCode;

public class ZipCodeItemWriter implements ItemWriter<ZipCode>, InitializingBean {

	@Autowired
	ZipCodeRepository zipCodeRepository;
	
	@Override
	public void afterPropertiesSet() throws Exception {		
	}

	@Override
	public void write(List<? extends ZipCode> items) throws Exception {
		
		for (ZipCode zipCode : items) {
			System.out.println("ZipCodeItemWriter" + zipCode.getZipCode());
			zipCodeRepository.save(zipCode);
		}
	}

}
