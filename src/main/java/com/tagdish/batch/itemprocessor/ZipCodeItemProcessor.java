package com.tagdish.batch.itemprocessor;

import org.springframework.batch.item.ItemProcessor;

import com.tagdish.batch.domain.BatchZipCode;
import com.tagdish.domain.elasticsearch.ZipCode;
import com.tagdish.domain.location.Location;

public class ZipCodeItemProcessor implements ItemProcessor<BatchZipCode, ZipCode> {

    @Override
    public ZipCode process(final BatchZipCode batchZipCode) throws Exception {
    	System.out.println("ZipCodeItemProcessor" + batchZipCode.getZipCode());
    	
    	ZipCode zipCode = new ZipCode();
    	zipCode.setZipCode(new Long(batchZipCode.getZipCode().replaceAll("\"", "")));
    	zipCode.setCity(batchZipCode.getCity().replaceAll("\"", ""));
    	zipCode.setCounty(batchZipCode.getCounty().replaceAll("\"", ""));
    	zipCode.setState(batchZipCode.getState().replaceAll("\"", ""));
    	Location loc = new Location();
    	loc.setLatitude(batchZipCode.getLatitude().replaceAll("\"", ""));
    	loc.setLongitude(batchZipCode.getLongtitude().replaceAll("\"", ""));
    	zipCode.setLocation(loc);
        return zipCode;
    }

}