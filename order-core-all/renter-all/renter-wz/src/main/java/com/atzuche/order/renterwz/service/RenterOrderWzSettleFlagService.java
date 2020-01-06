package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzSettleFlagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * RenterOrderWzSettleFlagService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzSettleFlagService {

    @Resource
    private RenterOrderWzSettleFlagMapper renterOrderWzSettleFlagMapper;

    public void updateIsIllegal(String orderNo, String carNum, int hasIllegal, String updateOp) {
        Integer count = renterOrderWzSettleFlagMapper.countByOrderNoAndCarNum(orderNo, carNum);
        if(count != null && count >0){
            renterOrderWzSettleFlagMapper.updateIsIllegal(orderNo,carNum,hasIllegal,updateOp);
        }else{
            addTransIllegalSettleFlag(orderNo,carNum,hasIllegal,null,updateOp);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int addTransIllegalSettleFlag(String orderNo,String carNum,Integer hasIllegal,Integer hasIllegalCost,String updateOp){
        if(orderNo==null) {
            return 0;
        }
        RenterOrderWzSettleFlagEntity entity=new RenterOrderWzSettleFlagEntity();
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNum);
        // 是否有违章：1-未通知，2-有违章，3-无违章
        entity.setHasIllegal(hasIllegal);
        // 是否有违章扣款金额：0-无，1-有
        entity.setHasIllegalCost(hasIllegalCost);
        entity.setUpdateOp(updateOp);
        return renterOrderWzSettleFlagMapper.saveRenterOrderWzSettleFlag(entity);
    }

    public int getIllegalSettleFlag(String orderNo, String carNum){
        Integer settleFlag=renterOrderWzSettleFlagMapper.getIllegalSettleFlag(orderNo,carNum);
        return settleFlag == null ? 0 : settleFlag;
    }

    public void updateIsIllegalCost(String orderNo, int hasIllegalCost, String updateOp, String carNum) {
        Integer count = renterOrderWzSettleFlagMapper.countByOrderNoAndCarNum(orderNo, carNum);
        if(count != null && count >0){
            renterOrderWzSettleFlagMapper.updateIsIllegalCost(orderNo,hasIllegalCost,updateOp,carNum);
        }else{
            addTransIllegalSettleFlag(orderNo,carNum,null,hasIllegalCost,updateOp);
        }
    }
}
