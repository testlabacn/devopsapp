package com.accenture.devops.repository.search;

import com.accenture.devops.domain.TrashBin;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TrashBin entity.
 */
public interface TrashBinSearchRepository extends ElasticsearchRepository<TrashBin, Long> {
}
