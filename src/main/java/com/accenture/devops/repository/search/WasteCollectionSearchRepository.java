package com.accenture.devops.repository.search;

import com.accenture.devops.domain.WasteCollection;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the WasteCollection entity.
 */
public interface WasteCollectionSearchRepository extends ElasticsearchRepository<WasteCollection, Long> {
}
