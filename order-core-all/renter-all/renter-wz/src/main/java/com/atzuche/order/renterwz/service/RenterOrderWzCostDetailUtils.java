package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 11:48 上午
 **/
public class RenterOrderWzCostDetailUtils {


    public static int getWzCostAmt(List<RenterOrderWzCostDetailEntity> list, WzCostEnums wzCostEnums){
        int total=0;
        for(RenterOrderWzCostDetailEntity costDetailEntity:list){
             if(costDetailEntity.getAmount()!=null&&wzCostEnums.getCode().equals(costDetailEntity.getCostCode())){
                 total = total+costDetailEntity.getAmount();
             }
        }
        return total;
    }
}
