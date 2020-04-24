package com.atzuche.order.admin.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/19 4:38 下午
 **/
@Data
@ToString
public class AdminTransferCarReqVO implements Serializable {
    @NotBlank(message="订单编号不能为空")
    @AutoDocProperty(value="订单编号,必填，",required=true)
    private String orderNo;

    @AutoDocProperty(value="车辆注册号")
    private String carNo;
    
    @NotBlank(message="车牌号不能为空")
    @AutoDocProperty(value="车牌号",required=true)
    private String plateNum;

    @AutoDocProperty(value="是否使用特供价 1-使用，0-不使用")
    private Integer useSpecialPriceFlag;

}
