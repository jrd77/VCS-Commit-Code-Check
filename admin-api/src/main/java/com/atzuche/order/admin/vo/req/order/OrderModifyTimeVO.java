package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 11:10 上午
 **/
@Data
public class OrderModifyTimeVO implements Serializable {
    /**
     * 订单号
     */
    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "新的订单开始时间，按照原来的格式")
    @NotBlank(message = "新的起租时间不能为空")
    private String newRentTime;
    @AutoDocProperty(value = "新的订单结束时间，按照原来的格式")
    @NotBlank(message = "新的还车时间不能为空")
    private String newRevertTime;

    @AutoDocProperty(value = "订单修改原因")
    private String modifyReason;
}
