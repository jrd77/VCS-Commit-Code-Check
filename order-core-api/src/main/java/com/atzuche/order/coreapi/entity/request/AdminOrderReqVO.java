package com.atzuche.order.coreapi.entity.request;

import lombok.Data;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 11:13 上午
 **/
@Data
public class AdminOrderReqVO extends NormalOrderReqVO {
    /**
     * 是否使用特供价
     */
    private String useSpecialPrice;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 管理后台相关标识
     */
    private String specialConsole;
    private String offlineOrderStatus;
}
