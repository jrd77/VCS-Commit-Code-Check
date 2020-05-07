package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.enums.ErrorCode;

/**
 * 库存相关业务校验异常
 *
 * @author pengcheng.fu
 * @date 2019/2/3 15:45
 */
public class ReleaseStockException extends SubmitOrderException  {

    private static final long serialVersionUID = 8357789422156063851L;

    public ReleaseStockException() {
        super(ErrorCode.RELEASE_STOCK_FAIL.getCode(), ErrorCode.RELEASE_STOCK_FAIL.getText());
    }
}
