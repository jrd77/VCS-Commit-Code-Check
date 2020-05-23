package com.atzuche.order.detain.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.orderDetailDto.RenterDetainReasonDTO;
import com.atzuche.order.commons.enums.detain.DetainClaimsReasonEnum;
import com.atzuche.order.commons.enums.detain.DetainRiskReasonEnum;
import com.atzuche.order.commons.enums.detain.DetainTransReasonEnum;
import com.atzuche.order.commons.enums.detain.DetainTypeEnum;
import com.atzuche.order.detain.entity.RenterDetainReasonEntity;
import com.atzuche.order.detain.mapper.RenterDetainReasonMapper;
import org.apache.commons.lang3.StringUtils;
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
            return initRenterDetainReason(orderNo);
        }

        List<RenterDetainReasonDTO> reasonList = new ArrayList<>();
        list.forEach(entity -> {
            RenterDetainReasonDTO dto = new RenterDetainReasonDTO();
            BeanUtils.copyProperties(entity, dto);
            reasonList.add(dto);
        });

        return reasonList;
    }



    private List<RenterDetainReasonDTO> initRenterDetainReason(String orderNo) {
        List<RenterDetainReasonDTO> reasonList = new ArrayList<>();
        //风控
        RenterDetainReasonDTO fk = new RenterDetainReasonDTO();
        fk.setOrderNo(orderNo);
        fk.setDetainTypeCode(DetainTypeEnum.risk.getCode());
        fk.setDetainTypeName(DetainTypeEnum.risk.getName());
        fk.setDetainStatus(OrderConstant.NO);
        //交易
        RenterDetainReasonDTO jy = new RenterDetainReasonDTO();
        jy.setOrderNo(orderNo);
        jy.setDetainTypeCode(DetainTypeEnum.trans.getCode());
        jy.setDetainTypeName(DetainTypeEnum.trans.getName());
        jy.setDetainStatus(OrderConstant.NO);
        //理赔
        RenterDetainReasonDTO lp = new RenterDetainReasonDTO();
        lp.setOrderNo(orderNo);
        lp.setDetainTypeCode(DetainTypeEnum.claims.getCode());
        lp.setDetainTypeName(DetainTypeEnum.claims.getName());
        lp.setDetainStatus(OrderConstant.NO);

        reasonList.add(fk);
        reasonList.add(jy);
        reasonList.add(lp);
        return reasonList;
    }

}
