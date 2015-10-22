package com.tagdish.batch.itemwriter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.tagdish.dao.repository.RestaurantRepository;
import com.tagdish.domain.elasticsearch.Restaurant;

public class RestaurantItemWriter implements ItemWriter<Restaurant> {

	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public void write(List<? extends Restaurant> items) throws Exception {
	
		for (Restaurant restaurant : items) {
			System.out.println("RestaurantItemWriter" + restaurant.getRestaurantId());

			createRestaurant(restaurant);
		}
	}
	
	private void createRestaurant(Restaurant restaurant) {
		
		Restaurant restaurantElasticSearch = null;
		restaurantElasticSearch = restaurantRepository.findByRestaurantId(restaurant.getRestaurantId());
		if(restaurantElasticSearch != null) {
			restaurantRepository.delete(restaurantElasticSearch);
		}
		
		// Insert the record only if is not deleted or active
		if(restaurant.getDeleted() == 0) {
			restaurantRepository.save(restaurant);	
		}
	}
}
