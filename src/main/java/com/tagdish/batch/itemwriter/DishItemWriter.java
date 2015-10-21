package com.tagdish.batch.itemwriter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.tagdish.dao.repository.DishRepository;
import com.tagdish.dao.repository.DishSearchRepository;
import com.tagdish.domain.elasticsearch.Dish;
import com.tagdish.domain.elasticsearch.DishSearch;

public class DishItemWriter implements ItemWriter<Dish> {

	@Autowired
	DishRepository dishRepository;
	
	@Autowired
	DishSearchRepository dishSearchRepository;	
	
	@Override
	public void write(List<? extends Dish> items) throws Exception {
	
		for (Dish dish : items) {
			System.out.println("DishItemWriter" + dish.getDishId());
			
			createDishSearch(dish);
			createDish(dish);

		}
	}
	
	private void createDishSearch(Dish dish) {
		
		DishSearch dishSearchElasticSearch = null;
		
		dishSearchElasticSearch = dishSearchRepository.findByDishId(dish.getDishId());
		if(dishSearchElasticSearch != null) {
			dishSearchRepository.delete(dishSearchElasticSearch);
		}
		dishSearchElasticSearch = new DishSearch();
		BeanUtils.copyProperties(dish, dishSearchElasticSearch);
		
		if(dishSearchElasticSearch.getZipCode() == null) {
			dishSearchElasticSearch.setZipCode(90503l);
		}
		
		dishSearchRepository.save(dishSearchElasticSearch);		
	}
	
	private void createDish(Dish dish) {
		
		Dish dishElasticSearch = null;
		dishElasticSearch = dishRepository.getDishById(dish.getDishId());
		if(dishElasticSearch != null) {
			dishRepository.delete(dishElasticSearch);
		}
		dishRepository.save(dish);		
	}

}
