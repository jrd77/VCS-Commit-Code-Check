package com.atzuche.order.open.service;

import com.atzuche.order.open.vo.OfflinePayVO;
import com.atzuche.order.open.vo.VirtualPayVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 5:17 下午
 **/
@FeignClient(name = "order-center-api")
public interface FeignPayService {

    @PostMapping("pay/virtual")
    public ResponseData virtualPay(@Valid @RequestBody VirtualPayVO virtualPayVO);

    @PostMapping("pay/offline")
    public ResponseData offlinePay(@Valid @RequestBody OfflinePayVO offlinePayVO);
}
