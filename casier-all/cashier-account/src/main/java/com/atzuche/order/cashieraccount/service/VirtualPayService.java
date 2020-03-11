package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.cashieraccount.service.notservice.AccountVirtualPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.VirtualPayDTO;
import com.atzuche.order.commons.service.OrderPayCallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 3:37 下午
 **/
@Service
public class VirtualPayService {

    @Autowired
    private AccountVirtualPayService accountVirtualPayService;

    @Autowired CashierPayService cashierPayService;



    @Transactional
    public void pay(VirtualPayDTO virtualPayDTO, OrderPayCallBack orderPayCallBack) {
        accountVirtualPayService.addVirtualPayRecord(virtualPayDTO);
        cashierPayService.virtualPay(virtualPayDTO,orderPayCallBack);
    }
}
