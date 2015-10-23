package com.tagdish.batch.itemprocessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tagdish.domain.db.DishDB;
import com.tagdish.domain.db.GeoTargetDB;
import com.tagdish.domain.elasticsearch.Dish;
import com.tagdish.domain.location.Address;
import com.tagdish.domain.location.Location;

public class DishItemProcessor implements ItemProcessor<DishDB, Dish> {

    @Override
    public Dish process(final DishDB dishDB) throws Exception {
    	System.out.println("DishItemProcessor" + dishDB.getDishId());
    	
    	Dish dish = null;
    	dish = new Dish();
    	BeanUtils.copyProperties(dishDB, dish);
    	
    	populateAddress(dishDB, dish);
    	populateLocation(dishDB, dish);

    	
    	
    	dish.setRestaurantId(dishDB.getMenuDB().getAccountDB().getAccountId());
    	dish.setRestaurantType(dishDB.getMenuDB().getAccountDB().getAccountType());
    	
        return dish;
    }

	private void populateAddress(DishDB dishDB, Dish dish) {

		Address address = null;
		GeoTargetDB geoTargetDB = null;
		if(dishDB.getMenuDB() != null && 
				dishDB.getMenuDB().getAccountDB() != null && 
				dishDB.getMenuDB().getAccountDB().getGeoTargetDB() != null) {

			address = new Address();
			geoTargetDB = dishDB.getMenuDB().getAccountDB().getGeoTargetDB();
			
			BeanUtils.copyProperties(geoTargetDB, address);
	    	dish.setAddress(address);
	    	dish.setZipCode(geoTargetDB.getZipcode());
		}
	}

	private void populateLocation(DishDB dishDB, Dish dish) {
		
    	Location location = null;
    	if(dishDB.getMenuDB() != null && dishDB.getMenuDB().getAccountDB() != null && 
    			dishDB.getMenuDB().getAccountDB().getGeoTargetDB() != null && 
    			dishDB.getMenuDB().getAccountDB().getGeoTargetDB().getLocation() != null) {
    		
    		location = new Location();
        	location.setLatitude(dishDB.getMenuDB().getAccountDB().getGeoTargetDB().getLocation().getLatitude());
        	location.setLongitude(dishDB.getMenuDB().getAccountDB().getGeoTargetDB().getLocation().getLongitude());
        	dish.setLocation(location);
    	}
	}
    
    

}