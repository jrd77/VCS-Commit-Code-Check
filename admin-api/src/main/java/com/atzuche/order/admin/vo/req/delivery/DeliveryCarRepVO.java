package com.atzuche.order.admin.vo.req.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Data
@ToString
public class DeliveryCarRepVO {

    @NotBlank(message="订单编号不能为空")
    @Pattern(regexp="^\\d*$",message="订单编号必须为数字")
    @AutoDocProperty(value="租客子订单编号,必填，",required=true)
    private String renterOrderNo;

}
