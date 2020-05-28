package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.stream.Stream;

@Data
public class PlatformToOwnerSubsidyDTO {
    @AutoDocProperty("超里程费用")
    private Integer mileageAmt;
    @AutoDocProperty("油费补贴")
    private Integer oilSubsidyAmt;
    @AutoDocProperty("洗车费补贴")
    private Integer washCarSubsidyAmt;
    @AutoDocProperty("车上物品损失补贴")
    private Integer carGoodsLossSubsidyAmt;
    @AutoDocProperty("延时补贴")
    private Integer delaySubsidyAmt;
    @AutoDocProperty("交通补贴")
    private Integer trafficSubsidyAmt;
    @AutoDocProperty("收益补贴")
    private Integer incomeSubsidyAmt;
    @AutoDocProperty("其他补贴")
    private Integer otherSubsidyAmt;
    @AutoDocProperty("总补贴")
    private Integer total;

    /*
     * @Author ZhangBin
     * @Date 2020/5/27 19:34
     * @Description: 记录日志时候设置默认值用到
     *
     **/
    public static void setDefaultValue(PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO){
        if(platformToOwnerSubsidyDTO == null){
            return;
        }
        platformToOwnerSubsidyDTO.setMileageAmt(platformToOwnerSubsidyDTO.getMileageAmt()==null?0:platformToOwnerSubsidyDTO.getMileageAmt());
        platformToOwnerSubsidyDTO.setOilSubsidyAmt(platformToOwnerSubsidyDTO.getOilSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getOilSubsidyAmt());
        platformToOwnerSubsidyDTO.setWashCarSubsidyAmt(platformToOwnerSubsidyDTO.getWashCarSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getWashCarSubsidyAmt());
        platformToOwnerSubsidyDTO.setCarGoodsLossSubsidyAmt(platformToOwnerSubsidyDTO.getCarGoodsLossSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getCarGoodsLossSubsidyAmt());
        platformToOwnerSubsidyDTO.setDelaySubsidyAmt(platformToOwnerSubsidyDTO.getDelaySubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getDelaySubsidyAmt());
        platformToOwnerSubsidyDTO.setTrafficSubsidyAmt(platformToOwnerSubsidyDTO.getTrafficSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getTrafficSubsidyAmt());
        platformToOwnerSubsidyDTO.setIncomeSubsidyAmt(platformToOwnerSubsidyDTO.getIncomeSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getIncomeSubsidyAmt());
        platformToOwnerSubsidyDTO.setOtherSubsidyAmt(platformToOwnerSubsidyDTO.getOtherSubsidyAmt()==null?0:platformToOwnerSubsidyDTO.getOtherSubsidyAmt());
    }



}
