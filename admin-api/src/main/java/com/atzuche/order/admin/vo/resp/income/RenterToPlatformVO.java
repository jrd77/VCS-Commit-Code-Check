/**
 * 
 */
package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 * 租客支付给平台的费用
 */
@Data
public class RenterToPlatformVO {
	@AutoDocProperty("油费")
    private String oliAmt;
    @AutoDocProperty("超时费用")
    private String timeOut;
    @AutoDocProperty("临时修改订单的时间和地址")
    private String modifyOrderTimeAndAddrAmt;
    @AutoDocProperty("车辆清洗费")
    private String carWash;
    @AutoDocProperty("延误等待费")
    private String dlayWait;
    @AutoDocProperty("停车费")
    private String stopCar;
    @AutoDocProperty("超里程费用")
    private String extraMileage;
    
}
