package com.tagdish.batch.itemprocessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tagdish.domain.db.AccountDB;
import com.tagdish.domain.db.GeoTargetDB;
import com.tagdish.domain.elasticsearch.Restaurant;
import com.tagdish.domain.location.Address;
import com.tagdish.domain.location.Location;

public class RestaurantItemProcessor implements ItemProcessor<AccountDB, Restaurant> {

    @Override
    public Restaurant process(final AccountDB accountDB) throws Exception {
    	System.out.println("RestaurantItemProcessor" + accountDB.getAccountId());
    	
    	Restaurant restaurant = null;
    	restaurant = new Restaurant();
    	BeanUtils.copyProperties(accountDB, restaurant);
    	
    	restaurant.setRestaurantId(accountDB.getAccountId());
    	restaurant.setRestaurantName(accountDB.getAccountName());
    	restaurant.setRestaurantType(accountDB.getAccountType());
    	
    	loadAddressInfo(accountDB.getGeoTargetDB(), restaurant);
    	
        return restaurant;
    }
    
    private void loadAddressInfo(GeoTargetDB geoTargetDB, Restaurant restaurant) {
    	
    	Address address = new Address();
    	
    	if(geoTargetDB != null) {

    		address = new Address();
        	BeanUtils.copyProperties(geoTargetDB, address);
        
        	loadLocationInfo(geoTargetDB.getLocation(), restaurant);
    	}    	
    }
    
    private void loadLocationInfo(Location dbLocation, Restaurant restaurant) {
    	
    	Location location = new Location();
    	if(dbLocation != null) {
        	location = new Location();
        	BeanUtils.copyProperties(dbLocation, location);        		
    	}    	
    }
}