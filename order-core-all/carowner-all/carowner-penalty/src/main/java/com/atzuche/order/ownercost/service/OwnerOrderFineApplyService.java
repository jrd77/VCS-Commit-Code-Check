package com.atzuche.order.ownercost.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.mapper.OwnerOrderFineApplyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author ZhangBin
 * @date 2020-01-14 19:39:44
 */
@Service
public class OwnerOrderFineApplyService {

    private static Logger logger = LoggerFactory.getLogger(OwnerOrderFineApplyService.class);

    @Autowired
    private OwnerOrderFineApplyMapper ownerOrderFineApplyMapper;


    public boolean addFineApplyRecord(OwnerOrderFineApplyEntity record) {
        logger.info("Add owner order fine apply record. param is,record:[{}]", JSON.toJSONString(record));

        int result = ownerOrderFineApplyMapper.insertSelective(record);

        logger.info("Add owner order fine apply record. result is,result:[{}],id:[{}]", result, record.getId());
        return result > 0;
    }


    public OwnerOrderFineApplyEntity getByOrderNo(String orderNo){
        return ownerOrderFineApplyMapper.selectByOrderNo(orderNo);
    }


}
