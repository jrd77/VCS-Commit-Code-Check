package com.atzuche.order.detain.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.detain.entity.RenterDetainReasonEntity;
import com.atzuche.order.detain.mapper.RenterDetainReasonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租车押金暂扣原因表
 *
 * @author ZhangBin
 * @date 2020-03-23 15:20:17
 */
@Service
public class RenterDetainReasonService {

    private static Logger logger = LoggerFactory.getLogger(RenterDetainReasonService.class);

    @Autowired
    private RenterDetainReasonMapper renterDetainReasonMapper;


    public int saveDetainReason(RenterDetainReasonEntity record) {
        logger.info("Save car deposit detain reason. param is,record:[{}]", JSON.toJSONString(record));
        RenterDetainReasonEntity reasonEntity = renterDetainReasonMapper.selectByOrderNo(record.getOrderNo(),
                record.getDetainTypeCode());

        logger.info("Save car deposit detain reason. param is,record:[{}]", JSON.toJSONString(record));
        if (null == reasonEntity) {
            //新增

        } else {
            //修改
        }


        return 0;
    }


}
