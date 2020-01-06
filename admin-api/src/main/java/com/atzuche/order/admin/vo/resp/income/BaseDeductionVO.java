package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 车主扣款
 */
@Data
@ToString
public class BaseDeductionVO {

    @AutoDocProperty("服务费")
    private String serviceCharge;
    @AutoDocProperty("车主需支付给平台的费用")
    private String ownerPayPlatFormFee;
    @AutoDocProperty("GPS押金")
    private String GPSAmt;
    @AutoDocProperty("平台加油服务费")
    private String platFormServiceCharge;
    @AutoDocProperty("GPS服务费")
    private String GPSServiceCharge;
    @AutoDocProperty("配送服务费")
    private String deliveryServiceCharge;

}
