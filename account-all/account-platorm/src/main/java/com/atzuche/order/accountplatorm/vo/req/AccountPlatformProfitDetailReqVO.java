package com.atzuche.order.accountplatorm.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 *  订单收益信息
 * @author haibao.yan
 */
@Data
public class AccountPlatformProfitDetailReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 费用
     */
    private Integer amt;
    /**
     * 费来源编码
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 费用产生凭证
     */
    private String uniqueNo;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;

    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());


    }
}
