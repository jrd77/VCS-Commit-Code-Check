package com.atzuche.order.delivery.service;

import com.atzuche.order.commons.exceptions.AdjustReasonException;
import com.atzuche.order.delivery.entity.OwnerRenterAdjustReasonEntity;
import com.atzuche.order.delivery.mapper.OwnerRenterAdjustReasonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 车主和租客相互调价原因和备注表
 *
 * @author ZhangBin
 * @date 2020-07-29 13:34:29
 */
@Slf4j
@Service
public class OwnerRenterAdjustReasonService{
    @Autowired
    private OwnerRenterAdjustReasonMapper ownerRenterAdjustReasonMapper;


    /*
     * @Author ZhangBin
     * @Date 2020/7/29 13:49
     * @Description: 存在数据就更新，不存在就插入
     *
     **/
    public void saveOrUpdateAdjustReason(OwnerRenterAdjustReasonEntity ownerRenterAdjustReasonEntity){
        if(ownerRenterAdjustReasonEntity==null){
            AdjustReasonException adjustReasonException = new AdjustReasonException();
            log.error("租客车主调价备注异常",adjustReasonException);
            throw adjustReasonException;
        }
        String ownerOrderNo = ownerRenterAdjustReasonEntity.getOwnerOrderNo();
        String renterOrderNo = ownerRenterAdjustReasonEntity.getRenterOrderNo();
        if(ownerOrderNo ==null || renterOrderNo ==null){
            AdjustReasonException adjustReasonException = new AdjustReasonException();
            log.error("租客车主调价备注异常",adjustReasonException);
            throw adjustReasonException;
        }
        OwnerRenterAdjustReasonEntity ownerRenterAdjustReasonDB = ownerRenterAdjustReasonMapper.selectByChildNo(ownerOrderNo, renterOrderNo);
        if(ownerRenterAdjustReasonDB == null || ownerRenterAdjustReasonDB.getId()==null){
            ownerRenterAdjustReasonMapper.insertSelective(ownerRenterAdjustReasonEntity);
        }else{
            ownerRenterAdjustReasonEntity.setId(ownerRenterAdjustReasonDB.getId());
            ownerRenterAdjustReasonMapper.updateByPrimaryKeySelective(ownerRenterAdjustReasonEntity);
        }
    }

}
