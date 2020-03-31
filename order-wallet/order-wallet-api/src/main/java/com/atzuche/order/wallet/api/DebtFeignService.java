package com.atzuche.order.wallet.api;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/16 6:04 下午
 **/
@FeignClient(name="order-wallet-service")
public interface DebtFeignService {
    /**
     * 返回用户的欠款，为正值
     * @param memNo
     * @return
     */
    @RequestMapping(value = "debt/get",method = RequestMethod.GET)
    public ResponseData<MemDebtVO> getMemBalance(@RequestParam("memNo")String memNo);

    /**
     * 扣减用户的欠款
     * @param deductDebtVO
     * @return
     */
    @RequestMapping(value = "debt/deduct",method = RequestMethod.POST)
    public ResponseData deductBalance(@Valid @RequestBody DeductDebtVO deductDebtVO);
}
