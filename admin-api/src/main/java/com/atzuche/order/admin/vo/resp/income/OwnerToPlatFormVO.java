package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 车主需支付给平台的费用
 */
@Data
@ToString
public class OwnerToPlatFormVO {

    @AutoDocProperty("超里程费用")
    private String extraMileageFee;
    @AutoDocProperty("油费")
    private String oilFee;
    @AutoDocProperty("车辆清洗费")
    private String carWashFee;
    @AutoDocProperty("停车费")
    private String stayCarFee;
    @AutoDocProperty("超时费")
    private String overTimeFee;
    @AutoDocProperty("临时修改订单时间、地址")
    private String temporaryModifyOrderFee;
    @AutoDocProperty("延误等待费")
    private String delayWaitFee;
    @ApiModelProperty(value="子订单号",required=true)
    private String renterOrderNo;

}
