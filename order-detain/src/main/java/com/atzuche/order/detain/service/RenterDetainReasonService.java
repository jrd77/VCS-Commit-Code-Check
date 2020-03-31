package com.atzuche.order.detain.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.orderDetailDto.RenterDetainReasonDTO;
import com.atzuche.order.detain.entity.RenterDetainReasonEntity;
import com.atzuche.order.detain.mapper.RenterDetainReasonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


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
        if (null == record) {
            logger.warn("Car deposit detain reason is empty.");
            return OrderConstant.ZERO;
        }
        RenterDetainReasonEntity reasonEntity = renterDetainReasonMapper.selectByOrderNo(record.getOrderNo(),
                record.getDetainTypeCode());

        logger.info("DB data, car deposit detain reason. data is,reasonEntity:[{}]", JSON.toJSONString(reasonEntity));
        int result;
        if (null == reasonEntity) {
            //新增
            record.setCreateOp(record.getUpdateOp());
            result = renterDetainReasonMapper.insertSelective(record);
        } else {
            //修改
            record.setId(reasonEntity.getId());
            result = renterDetainReasonMapper.updateByPrimaryKeySelective(record);
        }
        logger.info("Save car deposit detain reason. result is,result:[{}]", result);
        return result;
    }


    public List<RenterDetainReasonDTO> getListByOrderNo(String orderNo) {
        List<RenterDetainReasonEntity> list = renterDetainReasonMapper.selectListByOrderNo(orderNo);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<RenterDetainReasonDTO> reasonList = new ArrayList<>();
        list.forEach(entity -> {
            RenterDetainReasonDTO dto = new RenterDetainReasonDTO();
            BeanUtils.copyProperties(entity, dto);
            reasonList.add(dto);
        });

        return reasonList;
    }

}
