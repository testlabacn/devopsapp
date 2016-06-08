package com.accenture.devops.repository.search;

import com.accenture.devops.domain.Truck;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Truck entity.
 */
public interface TruckSearchRepository extends ElasticsearchRepository<Truck, Long> {
}
