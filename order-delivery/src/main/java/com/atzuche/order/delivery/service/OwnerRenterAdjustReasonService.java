package com.atzuche.order.delivery.service;

import com.atzuche.order.commons.entity.dto.OwnerRenterAdjustReasonDTO;
import com.atzuche.order.commons.exceptions.AdjustReasonException;
import com.atzuche.order.delivery.entity.OwnerRenterAdjustReasonEntity;
import com.atzuche.order.delivery.mapper.OwnerRenterAdjustReasonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        OwnerRenterAdjustReasonEntity ownerRenterAdjustReasonDB = ownerRenterAdjustReasonMapper.selectByChildNoAndadjustTarget(ownerOrderNo, renterOrderNo,ownerRenterAdjustReasonEntity.getAdjustTarget());
        if(ownerRenterAdjustReasonDB == null || ownerRenterAdjustReasonDB.getId()==null){
            ownerRenterAdjustReasonMapper.insertSelective(ownerRenterAdjustReasonEntity);
        }else{
            ownerRenterAdjustReasonEntity.setId(ownerRenterAdjustReasonDB.getId());
            ownerRenterAdjustReasonMapper.updateByPrimaryKeySelective(ownerRenterAdjustReasonEntity);
        }
    }

    public List<OwnerRenterAdjustReasonDTO> getOwnerRenterAdjustReasonByChildNo(String ownerOrderNo, String renterOrderNo){
        List<OwnerRenterAdjustReasonEntity> ownerRenterAdjustReasonEntities = ownerRenterAdjustReasonMapper.selectByChildNo(ownerOrderNo, renterOrderNo);
        List<OwnerRenterAdjustReasonDTO> list = new ArrayList<>();
        Optional.ofNullable(ownerRenterAdjustReasonEntities).orElseGet(ArrayList::new).forEach(x->{
            OwnerRenterAdjustReasonDTO ownerRenterAdjustReasonDTO = new OwnerRenterAdjustReasonDTO();
            BeanUtils.copyProperties(x,ownerRenterAdjustReasonDTO);
            list.add(ownerRenterAdjustReasonDTO);
        });
        return list;
    }

}
