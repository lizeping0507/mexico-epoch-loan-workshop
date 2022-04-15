package com.epoch.loan.workshop.common.dao.elastic;

import com.epoch.loan.workshop.common.entity.elastic.AccessLogElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.elastic
 * @className : AccessLogElasticDao
 * @createTime : 2022/3/28 18:33
 * @description : 响应日志
 */

public interface AccessLogElasticDao extends ElasticsearchRepository<AccessLogElasticEntity, Long> {
}
