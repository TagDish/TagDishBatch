package com.tagdish.batch.itemprocessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tagdish.domain.db.DishDB;
import com.tagdish.domain.elasticsearch.Dish;

public class DishItemProcessor implements ItemProcessor<DishDB, Dish> {

    @Override
    public Dish process(final DishDB dishDB) throws Exception {
    	System.out.println("DishItemProcessor" + dishDB.getDishId());
    	
    	Dish dish = null;
    	dish = new Dish();
    	BeanUtils.copyProperties(dishDB, dish);
        return dish;
    }

}