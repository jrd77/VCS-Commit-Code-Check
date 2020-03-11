package com.atzuche.order.coreapi.controller;

import com.atzuche.order.cashieraccount.common.PayCashTypeEnum;
import com.atzuche.order.cashieraccount.common.VirtualAccountEnum;
import com.atzuche.order.cashieraccount.common.VirtualPayTypeEnum;
import com.atzuche.order.cashieraccount.service.VirtualPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.VirtualPayDTO;
import com.atzuche.order.open.vo.VirtualPayVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.coreapi.service.PayCallbackService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 3:16 下午
 **/
@RestController
public class VirtualPayController {
    private final static Logger logger = LoggerFactory.getLogger(VirtualPayController.class);
    

    @Autowired
    private VirtualPayService virtualPayService;

    @Autowired
    private OrderService orderService;

    @Autowired PayCallbackService payCallbackService;

    @Autowired
    private RenterOrderService renterOrderService;

    @PostMapping("pay/virtual")
    public ResponseData  virtualPay(@Valid @RequestBody VirtualPayVO virtualPayVO, BindingResult result) {
        logger.info("virtualPay param is [{}]",virtualPayVO);
        BindingResultUtil.checkBindingResult(result);
        if (virtualPayVO.getPayAmt() <= 0) {
            throw new InputErrorException("payAmt>=0:" + virtualPayVO.getPayAmt());
        }

        OrderEntity orderEntity = orderService.getOrderEntity(virtualPayVO.getOrderNo());
        if (orderEntity == null) {
            throw new OrderNotFoundException(virtualPayVO.getOrderNo());
        }

        RenterOrderEntity renterOrderEntity =renterOrderService.getRenterOrderByOrderNoAndIsEffective(virtualPayVO.getOrderNo());
        if (renterOrderEntity == null) {
            throw new OrderNotFoundException(virtualPayVO.getOrderNo());
        }

        VirtualPayDTO virtualPayDTO = new VirtualPayDTO();
        virtualPayDTO.setAccountEnum(VirtualAccountEnum.fromAccountNo(virtualPayVO.getAccountNo()));
        virtualPayDTO.setCashType(PayCashTypeEnum.fromValue(virtualPayVO.getCashType()));
        virtualPayDTO.setOrderNo(virtualPayVO.getOrderNo());
        virtualPayDTO.setPayType(VirtualPayTypeEnum.PAY);
        virtualPayDTO.setMemNo(orderEntity.getMemNoRenter());
        virtualPayDTO.setPayAmt(virtualPayVO.getPayAmt());
        virtualPayDTO.setRenterNo(renterOrderEntity.getRenterOrderNo());

        virtualPayService.pay(virtualPayDTO, payCallbackService);

        return ResponseData.success();
    }
}
