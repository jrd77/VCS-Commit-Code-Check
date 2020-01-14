package com.atzuche.order.accountrenterrentcost.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 租客费用总表落库 请求参数
 * @author haibao.yan
 */
@Data
public class AccountRenterCostToFineReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 租车费用
     */
    private Integer amt;
    /**
     * 车主子订单
     */
    private String renterOrderNo;

}
