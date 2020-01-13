package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzSettleFlagMapper;
import org.apache.commons.collections.CollectionUtils;
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

    /**
     * 结算成功
     */
    private static final int SETTLE_FLAG_SUCCESS=1;
    /**
     * 结算失败
     */
    private static final int SETTLE_FLAG_ERROR=2;


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

    public void updateSettle(String orderNo) {
        //结算成功后的处理
        this.updateIllegalSettleFlagSuccess(orderNo);
        List<RenterOrderWzStatusEntity> list = renterOrderWzStatusService.queryInfosByOrderNo(orderNo);
        if(CollectionUtils.isNotEmpty(list)){
            for (RenterOrderWzStatusEntity dto : list) {
                //结算时，违章处理状态为“未处理”的，处理状态改为“已处理-无信息”
                if(dto!= null && dto.getStatus() != null && dto.getStatus().equals(5)){
                    renterOrderWzStatusService.updateStatusByOrderNoAndCarNum(dto.getOrderNo(),45,dto.getCarPlateNum());
                }
                //结算时，违章处理状态为“待租客处理”的，处理状态改为“处理中-租客处理”（违章钱振庭需求）
                if(dto!= null && dto.getStatus() != null && dto.getStatus().equals(10)){
                    renterOrderWzStatusService.updateStatusByOrderNoAndCarNum(dto.getOrderNo(),25,dto.getCarPlateNum());
                }
            }
        }
    }

    private void updateIllegalSettleFlagSuccess(String orderNo) {
        renterOrderWzSettleFlagMapper.updateSettleFlag(orderNo,SETTLE_FLAG_SUCCESS,"违章结算定时任务");
    }

    public List<RenterOrderWzSettleFlagEntity> getIllegalSettleInfosByOrderNo(String orderNo) {
        return renterOrderWzSettleFlagMapper.getIllegalSettleInfosByOrderNo(orderNo);
    }
}
