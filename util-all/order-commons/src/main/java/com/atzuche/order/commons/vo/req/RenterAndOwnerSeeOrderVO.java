package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class RenterAndOwnerSeeOrderVO {
    @NotBlank(message = "主订单号不能为空")
    @AutoDocProperty("主订单号")
    private String orderNo;

    @AutoDocProperty("车主会员号")
    private String ownerMemeNo;

    @NotNull(message = "来源类型不能为空")
    @AutoDocProperty("来源类型，1：车主 ，2：租客")
    private Integer sourceType;
}
