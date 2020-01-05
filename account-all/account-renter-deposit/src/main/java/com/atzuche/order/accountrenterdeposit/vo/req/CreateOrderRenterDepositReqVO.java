package com.atzuche.order.accountrenterdeposit.vo.req;

import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;


/**
 * 下单成功 记录应付车俩押金
 */
@Data
public class CreateOrderRenterDepositReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 应付押金总额
     */
    private Integer yingfuDepositAmt;
    /**
     * 免押金额
     */
    private Integer reductionAmt;


    /**
     * 免押方式
     */
    private FreeDepositTypeEnum freeDepositType;

    /**
     * 租车押金:01,违章押金:02,补付租车押金:03,坦客-租车费用:04,坦客-押金费用:05,充值:06,欠款:07,补付租车押金,管理后台v5.11:08,长租线上费用支付:09,PMS:10,默认:99
     */
    private String payKind;

    /**
     * 支付业务，只对接业务的APPID 短租：20,长租：21,PMS：22,套餐：23
     */
    private String atappId;



    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getYingfuDepositAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue( Math.abs(getYingfuDepositAmt()) > 0, ErrorCode.PARAMETER_ERROR.getText());
        //是否存在减免
        Assert.notNull(getFreeDepositType(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
