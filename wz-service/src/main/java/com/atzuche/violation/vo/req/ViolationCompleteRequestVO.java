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
public class ViolationCompleteRequestVO {

    @AutoDocProperty(value = "主订单号")
    @NotBlank(message = "主订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "违章处理方,1:租客自行办理违章 2:凹凸代为办理违章 3:车主自行办理 4:无数据")
    @NotBlank(message = "违章处理方不能为空")
    private String managementMode; //违章处理方

}
