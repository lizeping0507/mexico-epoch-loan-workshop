package com.epoch.loan.workshop.common.dao.elastic;

import com.epoch.loan.workshop.common.entity.elastic.OcrLivingDetectionLogElasticEntity;
import com.epoch.loan.workshop.common.entity.elastic.SdkCatchDataSyncLogElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.dao.elastic
 * @className : SdkCatchDataSyncLogElasticDao
 * @createTime : 2022/04/26 17:15
 * @Description:
 */
public interface SdkCatchDataSyncLogElasticDao extends ElasticsearchRepository<SdkCatchDataSyncLogElasticEntity, Long> {
}
