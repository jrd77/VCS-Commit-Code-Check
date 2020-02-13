package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 11:55 上午
 **/
@Data
@ToString
public class RenterWzCostVO {
    @AutoDocProperty(value = "协助违章处理费")
    private Integer wzFineAmt=0;
    @AutoDocProperty(value = "不良用车处罚金")
    private Integer wzDysFineAmt=0;
    @AutoDocProperty(value = "凹凸代办服务费")
    private Integer wzServiceCostAmt=0;
    @AutoDocProperty(value = "停运费")
    private Integer wzStopCostAmt=0;
    @AutoDocProperty(value = "其他扣款")
    private Integer wzOtherAmt=0;
    @AutoDocProperty(value = "保险理赔")
    private Integer wzInsuranceAmt=0;

    @AutoDocProperty(value = "违章费用总额")
    private Integer totalWzCostAmt;

    public Integer getTotalWzCostAmt(){
        return wzFineAmt+wzDysFineAmt+wzServiceCostAmt+wzStopCostAmt+wzOtherAmt+wzInsuranceAmt;
    }
}
