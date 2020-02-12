/**
 * 
 */
package com.atzuche.order.commons.enums.detain;

import lombok.Getter;

/**
 * @author jing.huang
 * 暂扣来源  暂不使用，使用租客的费用编码来定义。
 * entity.setSourceCode(Integer.parseInt(detainRenterDeposit.getRenterCashCodeEnum().getCashNo()));
        entity.setSourceDetail(detainRenterDeposit.getRenterCashCodeEnum().getTxt());
 */
@Getter
public enum DetailSourceEnum {
	/**
     * 租车押金
     **/
    RENT_DEPOSIT(1, "租车押金"),
    /**
     * 违章押金
     **/
    WZ_DEPOSIT(2, "违章押金"),

    ;

    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;

    DetailSourceEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
