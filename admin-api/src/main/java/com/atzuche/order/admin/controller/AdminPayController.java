package com.atzuche.order.admin.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.open.service.PayFeignService;
import com.atzuche.order.open.vo.VirtualPayVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 5:22 下午
 **/
@RestController
public class AdminPayController {

    @Autowired
    private PayFeignService payFeignService;

    public ResponseData virtualPay(@Valid @RequestBody VirtualPayVO virtualPayVO, BindingResult result){
        BindingResultUtil.checkBindingResult(result);
        payFeignService.virtualPay(virtualPayVO);
        return ResponseData.success();

    }
}
