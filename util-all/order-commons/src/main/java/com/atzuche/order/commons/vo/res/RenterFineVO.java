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
public  class RenterFineVO{
    @AutoDocProperty(value = "修改订单取车违约金")
    private Integer modifyGetFine;
    @AutoDocProperty(value = "修改订单还车违约金")
    private Integer modifyReturnFine;
    @AutoDocProperty(value = "修改订单提前还车违约金")
    private Integer modifyAdvanceFine;
    @AutoDocProperty(value = "取消订单违约金")
    private Integer cancelFine;
    @AutoDocProperty(value = "延迟还车违约金")
    private Integer delayFine;
    @AutoDocProperty(value = "租客提前还车罚金")
    private Integer renterAdvanceReturnFine;
   //@AutoDocProperty(value = "租客延迟还车罚金")
    //private Integer renterDelayReturnFine;
    @AutoDocProperty(value = "取还车违约金")
    private Integer getReturnCarFine;



    private Integer totalFine;

    public Integer getTotalFine(){
        return modifyGetFine+modifyReturnFine+modifyAdvanceFine+cancelFine+delayFine+renterAdvanceReturnFine+/*renterDelayReturnFine+*/getReturnCarFine;
    }
}
