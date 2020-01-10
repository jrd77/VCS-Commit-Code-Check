package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;
import com.atzuche.order.renterwz.mapper.RenterOrderWzCostDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * RenterOrderWzCostDetailService
 *
 * @author shisong
 * @date 2019/12/31
 */
@Service("renterOrderWzCostDetailService")
public class RenterOrderWzCostDetailService {

    @Resource
    private RenterOrderWzCostDetailMapper renterOrderWzCostDetailMapper;

    private static final String SOURCE_TYPE_REN_YUN = "1";
    private static final String REN_YUN_NAME = "任云";

    private static final Integer LOSE_EFFECTIVENESS = 1;

    public int updateTransFeeByOrderNo(String orderNo, Integer wzFine, Integer wzDysFine, Integer wzServiceCost, String carNum, Integer memNo) {
        int count = 0;
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(1));
        if(wzFine != null && wzFine > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(1, orderNo, wzFine, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(2));
        if(wzDysFine != null && wzDysFine > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(2, orderNo, wzDysFine, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(3));
        if(wzServiceCost != null && wzServiceCost > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(3, orderNo, wzServiceCost, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        return count;
    }

    private RenterOrderWzCostDetailEntity getEntityByType(Integer type,String orderNo,Integer amount,String carNum, Integer memNo){
        RenterOrderWzCostDetailEntity entity = new RenterOrderWzCostDetailEntity();
        entity.setAmount(amount);
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNum);
        entity.setMemNo(memNo);
        entity.setCostCode(WzCostEnums.getCode(type));
        entity.setCostDesc(WzCostEnums.getDesc(type));
        entity.setCreateTime(new Date());
        entity.setSourceType(SOURCE_TYPE_REN_YUN);
        entity.setOperatorName(REN_YUN_NAME);
        entity.setCreateOp(REN_YUN_NAME);
        return entity;
    }

    public RenterOrderWzCostDetailEntity queryInfoByOrderAndCode(String orderNo, String costCode) {
        return renterOrderWzCostDetailMapper.queryInfoByOrderAndCode(orderNo,costCode);
    }

    public void updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(String orderNo, String carNum, Integer memNo, Integer costStatus, String code){
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,costStatus,code);
    }

    public void saveRenterOrderWzCostDetail(RenterOrderWzCostDetailEntity renterOrderWzCostDetail){
        renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(renterOrderWzCostDetail);
    }

    public List<RenterOrderWzCostDetailEntity> queryInfosByOrderNo(String orderNo) {
        return renterOrderWzCostDetailMapper.queryInfosByOrderNo(orderNo);
    }

    //查询能否违章能否结算
    /*public*/
}
