package com.tagdish.batch.itemprocessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tagdish.domain.db.AccountDB;
import com.tagdish.domain.elasticsearch.Restaurant;

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
    	
        return restaurant;
    }

}