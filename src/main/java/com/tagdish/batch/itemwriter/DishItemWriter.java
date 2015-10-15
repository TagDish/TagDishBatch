package com.tagdish.batch.itemwriter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.tagdish.dao.repository.DishRepository;
import com.tagdish.domain.elasticsearch.Dish;

public class DishItemWriter implements ItemWriter<Dish> {

	@Autowired
	DishRepository dishRepository;
	
	@Override
	public void write(List<? extends Dish> items) throws Exception {
		
		Dish dbDish = null;
		for (Dish dish : items) {
			System.out.println("DishItemWriter" + dish.getDishId());
			
			dbDish = dishRepository.getDishById(dish.getDishId());
			if(dbDish != null) {
				dishRepository.delete(dbDish);
			}
			dishRepository.save(dish);
		}
	}

}
