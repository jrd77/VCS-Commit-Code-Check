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

    @AutoDocProperty(value = "钱包余额,如:500")
    private Integer walletBal;


    public WalletReductionVO() {
    }

    public WalletReductionVO(Integer walletBal) {
        this.walletBal = walletBal;
    }

    public Integer getWalletBal() {
        return walletBal;
    }

    public void setWalletBal(Integer walletBal) {
        this.walletBal = walletBal;
    }

}
