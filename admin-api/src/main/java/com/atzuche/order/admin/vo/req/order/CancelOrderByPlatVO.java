package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 11:21 上午
 **/
@Data
@ToString
public class CancelOrderByPlatVO {
    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "取消原因,如:0,租客审核不通过（风控）1,车载设备有问题 2,车辆已下架 3,租客取消不产生违约金（代步车）4,测试订单（交易）5,机械故障结束订单（交易）6,调度成功重新成单（交易）等",
            required = true)
    @NotBlank(message = "取消原因不能为空")
    private String cancelType;

    @AutoDocProperty(value = "操作人", required = true)
    private String operator;
}
