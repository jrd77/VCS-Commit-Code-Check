package com.atzuche.order;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;

import java.util.List;

/**
 * 从费用详情里分离出指定的费用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 3:14 下午
 **/
public class RenterOrderCostDetailUtils {

    /**
     * 从列表中返回指定编码的费用类型
     * @param costDetailEntityList
     * @param cashCodeEnum
     * @return
     */
    public static int getAmt(List<RenterOrderCostDetailEntity> costDetailEntityList,RenterCashCodeEnum cashCodeEnum){
        int total=0;
        for(RenterOrderCostDetailEntity entity:costDetailEntityList){
            if(entity.getCostCode()!=null&& cashCodeEnum.getCashNo().equals(entity.getCostCode())){
                if(entity.getTotalAmount()!=null) {
                    total = total + entity.getTotalAmount();
                }
            }
        }
        return total;
    }

    /**
     * 从列表中获得租金总额（没有减免、不包含其他费用）(负值）
     * @param costDetailEntityList
     * @return
     */
    public static int getRentAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.RENT_AMT);
    }



    /**
     * 从列表中获得平台保障费总额(负值）
     * @param costDetailEntityList
     * @return
     */
    public static int getInsuranceAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.INSURE_TOTAL_PRICES);

    }

    /**
     * 从列表中获得全面保障费总额(负值）
     * @param costDetailEntityList
     * @return
     */
    public static int getAbatementInsuranceAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.ABATEMENT_INSURE);
    }

    /**
     * 从列表中获得手续费
     * @param costDetailEntityList
     * @return
     */
    public static int getFeeAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.FEE);
    }

    /**
     * 从列表中获得取车费用
     * @param costDetailEntityList
     * @return
     */
    public static int getSrvGetCostAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){

        return getAmt(costDetailEntityList,RenterCashCodeEnum.SRV_GET_COST);

    }

    /**
     * 从列表中获得还车费用
     * @param costDetailEntityList
     * @return
     */
    public static int getSrvReturnCostAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.SRV_RETURN_COST);
    }


    /**
     * 从列表中获得附加驾驶人费用
     * @param costDetailEntityList
     * @return
     */
    public static int getExtraDriverInsureAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.EXTRA_DRIVER_INSURE);
    }

    /**
     * 从列表中获取取车运能加价
     * @param costDetailEntityList
     * @return
     */
    public static int getGetBlockedRaiseAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT);
    }

    /**
     * 从列表中获取还车运能加价
     * @param costDetailEntityList
     * @return
     */
    public static int getReturnBlockedRaiseAmt(List<RenterOrderCostDetailEntity> costDetailEntityList){
        return getAmt(costDetailEntityList,RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT);
    }



}
