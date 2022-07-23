package com.epoch.loan.workshop.common.dao.elastic;

import com.epoch.loan.workshop.common.entity.elastic.AfCallBackLogElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.dao.elastic
 * @className : AfCallbackLogElasticDao
 * @createTime : 2022/07/23 16:28
 * @Description:
 */
public interface AfCallBackLogElasticDao extends ElasticsearchRepository<AfCallBackLogElasticEntity, Long> {
}
