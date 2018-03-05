package com.antt.repository.search;

import com.antt.domain.Right;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Right entity.
 */
public interface RightSearchRepository extends ElasticsearchRepository<Right, Long> {
}
