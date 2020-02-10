package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/9 4:06 下午
 **/
@Data
@ToString
public class RenterDeliveryFeeDetailVO {
    @AutoDocProperty(value = "取车高峰运能费总额")
    private Integer getBlockedRaiseAmt=0;
    @AutoDocProperty(value = "还车高峰运能费总额")
    private Integer returnBlockedRaiseAmt;
    @AutoDocProperty(value = "取车服务费总额")
    private Integer srvGetCostAmt=0;
    @AutoDocProperty(value = "还车服务费总额")
    private Integer srvReturnCostAmt=0;

    @AutoDocProperty(value = "配送费总额")
    private Integer deliveryTotal=0;

    public Integer getDeliveryTotal(){
        return this.srvGetCostAmt+this.srvReturnCostAmt+this.getBlockedRaiseAmt+this.returnBlockedRaiseAmt;
    }
}
