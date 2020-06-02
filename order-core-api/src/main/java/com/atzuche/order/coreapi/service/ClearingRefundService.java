package com.atzuche.order.coreapi.service;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.coreapi.entity.request.ClearingRefundReqVO;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClearingRefundService {
    @Autowired
    private CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired
    private CashierPayService cashierPayService;

    public void clearingRefundSubmitToQuery(ClearingRefundReqVO clearingRefundReqVO) {

    }


    public ResponseData<?> clearingRefundSubmitToRefund(ClearingRefundReqVO clearingRefundReqVO) {
        CashierRefundApplyEntity cashierRefundApplyEntity = cashierRefundApplyNoTService.selectByOrerNoAndPayTransNo(clearingRefundReqVO.getOrderNo(),clearingRefundReqVO.getPayTransNo());
        cashierPayService.refundOrderPay(cashierRefundApplyEntity);
        return null;
    }
}
