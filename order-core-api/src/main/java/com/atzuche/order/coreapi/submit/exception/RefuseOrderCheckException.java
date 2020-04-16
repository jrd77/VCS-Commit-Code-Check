package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.OrderException;
import com.autoyol.commons.web.ErrorCode;

/**
 * RefuseOrderController 相关业务校验异常
 *
 * @author pengcheng.fu
 * @date 2019/2/3 15:45
 */
public class RefuseOrderCheckException extends OrderException {


    private static final long serialVersionUID = 7857594134974477358L;

    public RefuseOrderCheckException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public RefuseOrderCheckException() {
        super(ErrorCode.OWNER_COMFIRM_TRANS_FAILED.getCode(), ErrorCode.OWNER_COMFIRM_TRANS_FAILED.getText());
    }

    public RefuseOrderCheckException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getText());
    }
}