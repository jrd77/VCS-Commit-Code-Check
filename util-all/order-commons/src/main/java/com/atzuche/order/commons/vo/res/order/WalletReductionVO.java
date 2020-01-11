package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 钱包抵扣信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class WalletReductionVO implements Serializable {

    private static final long serialVersionUID = -6989562783037570177L;

    @AutoDocProperty(value = "钱包抵扣额，返回的是负值，如-20,如果为0，需要改为-0")
    private String walletDeductibleAmt;
    @AutoDocProperty(value = "使用钱包余额描述,如:使用充值余额抵扣(余额xxx元)")
    private String walletUseText;
    @AutoDocProperty(value = "(有500元可用),如果没有可用钱包余额，无需返回")
    private String walletText;
    @AutoDocProperty(value = "钱包余额,如:500")
    private String walletBal;

    public String getWalletDeductibleAmt() {
        return walletDeductibleAmt;
    }

    public String getWalletUseText() {
        return walletUseText;
    }

    public String getWalletText() {
        return walletText;
    }

    public String getWalletBal() {
        return walletBal;
    }

    public void setWalletDeductibleAmt(String walletDeductibleAmt) {
        this.walletDeductibleAmt = walletDeductibleAmt;
    }

    public void setWalletUseText(String walletUseText) {
        this.walletUseText = walletUseText;
    }

    public void setWalletText(String walletText) {
        this.walletText = walletText;
    }

    public void setWalletBal(String walletBal) {
        this.walletBal = walletBal;
    }

}
