package com.atzuche.order.wallet.api;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:14 下午
 **/
@FeignClient(name="order-wallet-service")
public interface AccountFeignService {
    /**
     * 返回用户名下所有的绑卡信息
     * @param memNo
     * @return
     */
    @RequestMapping(value = "account/get",method = RequestMethod.GET)
    public ResponseData<MemAccount> findAccountByMemNo(@RequestParam("memNo") String memNo);
}
