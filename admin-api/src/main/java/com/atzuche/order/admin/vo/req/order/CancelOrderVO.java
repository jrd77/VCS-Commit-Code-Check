package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 11:27 上午
 **/
@Data
public class CancelOrderVO  {
    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "使用角色:1.车主 2.租客", required = true)
    @NotBlank(message = "使用角色不能为空")
    private String memRole;

    @AutoDocProperty(value = "取消原因")
    private String cancelReason;

}
