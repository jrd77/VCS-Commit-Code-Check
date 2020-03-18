package com.atzuche.violation.service;


import com.atzuche.violation.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.violation.entity.RenterOrderWzStatusEntity;
import com.atzuche.violation.mapper.RenterOrderWzSettleFlagMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private RenterOrderWzStatusService renterOrderWzStatusService;

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


}
