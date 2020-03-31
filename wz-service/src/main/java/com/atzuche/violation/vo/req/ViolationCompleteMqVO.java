package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by qincai.lin on 2020/3/16.
 */
@Data
@ToString
public class ViolationCompleteMqVO {

    @AutoDocProperty(value = "主订单号")
    @NotBlank(message = "主订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人")
    private String operator;

}
