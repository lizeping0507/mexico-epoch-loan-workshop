package com.epoch.loan.workshop.common.dao.elastic;

import com.epoch.loan.workshop.common.entity.elastic.OcrAdvanceLogElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.dao.elastic
 * @className : OcrLivingDetectionLogElasticDao
 * @createTime : 2022/04/20 15:59
 * @Description: advance 活体检测日志
 */
public interface OcrAdvanceLogElasticDao extends ElasticsearchRepository<OcrAdvanceLogElasticEntity, Long> {
}
