package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/9 5:13 下午
 **/
@Data
@ToString
public class RenterSubsidyDetailVO {
    @AutoDocProperty(value = "平台优惠券补贴")
    private Integer platformCouponSubsidyAmt;
    @AutoDocProperty(value = "取还车优惠券补贴")
    private Integer getCarCouponSubsidyAmt;
    @AutoDocProperty(value = "车主优惠券补贴")
    private Integer ownerCouponSubsidyAmt;
    @AutoDocProperty(value = "凹凸币补贴")
    private Integer autoCoinSubsidyAmt;
    @AutoDocProperty(value = "升级补贴")
    private Integer updateSubsidyAmt;
    @AutoDocProperty(value = "限时红包补贴")
    private Integer limitTimeSubsidyAmt;


    @AutoDocProperty(value = "租金补贴")
    private Integer rentSubsidyAmt;
    @AutoDocProperty(value = "租客给车主的调价补贴")
    private Integer renter2OwnerSubsidyAmt;
    @AutoDocProperty(value = "车主给租客的调价补贴")
    private Integer owner2RenterSubsidyAmt;
    @AutoDocProperty(value = "手续费补贴")
    private Integer feeSubsidyAmt;
    @AutoDocProperty(value = "全面保障服务费补贴")
    private Integer abatementSubsidyAmt;
    @AutoDocProperty(value = "其他补贴")
    private Integer otherSubsidyAmt;
    @AutoDocProperty(value = "基础保障费补贴")
    private Integer insureSubsidyAmt;
    @AutoDocProperty(value = "油费补贴")
    private Integer oilSubsidyAmt;
    @AutoDocProperty(value = "延时补贴")
    private Integer delaySubsidyAmt;
    @AutoDocProperty(value = "取还车迟到补贴")
    private Integer getReturnDelaySubsidyAmt;
    @AutoDocProperty(value = "洗车费补贴")
    private Integer cleanCarSubsidyAmt;
    @AutoDocProperty(value = "车主给租客的租金")
    private Integer owner2RenterRentSubsidyAmt;
    @AutoDocProperty(value = "交通费补贴")
    private Integer trafficSubsidyAmt;
    @AutoDocProperty(value = "补贴总额")
    private Integer totalSubsidy;
    @AutoDocProperty(value = "租客给平台的费用")
    private Integer renter2PlatformAmt;
    @AutoDocProperty(value = "长租折扣金额")
    private Integer longRentDecutAmt;
    @AutoDocProperty(value = "长租-取还车费用补贴")
    private Integer longGetReturnCarCostSubsidy;

    @AutoDocProperty(value = "取车服务费补贴")
    private Integer srvGetCostAmt=0;
    @AutoDocProperty(value = "还车服务费补贴")
    private Integer srvReturnCostAmt=0;

    public Integer getTotalSubsidy(){
        int total=0;
        if(platformCouponSubsidyAmt!=null){
            total=total+platformCouponSubsidyAmt;
        }
        if(getCarCouponSubsidyAmt!=null){
            total=total+getCarCouponSubsidyAmt;
        }
        if(ownerCouponSubsidyAmt!=null){
            total=total+ownerCouponSubsidyAmt;
        }
        if(autoCoinSubsidyAmt!=null){
            total=total+autoCoinSubsidyAmt;
        }
        if(updateSubsidyAmt!=null){
            total=total+updateSubsidyAmt;
        }
        if(limitTimeSubsidyAmt!=null){
            total=total+limitTimeSubsidyAmt;
        }
        if(rentSubsidyAmt!=null){
            total=total+rentSubsidyAmt;
        }

        if(renter2OwnerSubsidyAmt!=null){
            total=total+renter2OwnerSubsidyAmt;
        }
        if(owner2RenterSubsidyAmt!=null){
            total=total+owner2RenterSubsidyAmt;
        }
        if(feeSubsidyAmt!=null){
            total=total+feeSubsidyAmt;
        }
        if(abatementSubsidyAmt!=null){
            total=total+abatementSubsidyAmt;
        }
        if(otherSubsidyAmt!=null){
            total=total+otherSubsidyAmt;
        }
        if(insureSubsidyAmt!=null){
            total=total+insureSubsidyAmt;
        }
        if(oilSubsidyAmt!=null){
            total=total+oilSubsidyAmt;
        }
        if(delaySubsidyAmt!=null){
            total=total+delaySubsidyAmt;
        }
        if(getReturnDelaySubsidyAmt!=null){
            total=total+getReturnDelaySubsidyAmt;
        }
        if(cleanCarSubsidyAmt!=null){
            total=total+cleanCarSubsidyAmt;
        }
        if(owner2RenterRentSubsidyAmt!=null){
            total=total+owner2RenterRentSubsidyAmt;
        }
        if(trafficSubsidyAmt!=null){
            total=total+trafficSubsidyAmt;
        }
        if(srvGetCostAmt != null){
            total=total+srvGetCostAmt;
        }
        if(srvReturnCostAmt != null){
            total=total+srvReturnCostAmt;
        }
        if(longRentDecutAmt != null){
            total=total+longRentDecutAmt;
        }
        return total;
    }






}
