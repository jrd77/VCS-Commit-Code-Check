package com.atzuche.order.admin.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.open.service.FeignPayService;
import com.atzuche.order.open.vo.OfflinePayVO;
import com.atzuche.order.open.vo.VirtualPayVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
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
    private FeignPayService payFeignService;

    @AutoDocMethod(description = "虚拟支付", value = "虚拟支付")
    @PostMapping("console/pay/virtual")
    public ResponseData virtualPay(@Valid @RequestBody VirtualPayVO virtualPayVO, BindingResult result){
        BindingResultUtil.checkBindingResult(result);
        payFeignService.virtualPay(virtualPayVO);
        return ResponseData.success();
    }
    @AutoDocMethod(description = "线下支付", value = "线下支付")
    @PostMapping("console/pay/offline")
    public ResponseData offlinePay(@Valid @RequestBody OfflinePayVO offlinePayVO, BindingResult result){
        BindingResultUtil.checkBindingResult(result);
        payFeignService.offlinePay(offlinePayVO);
        return ResponseData.success();
    }
}
