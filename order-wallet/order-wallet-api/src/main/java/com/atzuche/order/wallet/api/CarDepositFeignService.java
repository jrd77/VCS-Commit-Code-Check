package com.atzuche.order.wallet.api;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 2:26 下午
 **/
@FeignClient(name="order-wallet-service")
public interface CarDepositFeignService {
    @RequestMapping(value = "carDeposit/getByCarNo",method = RequestMethod.GET)
    public ResponseData<CarDepositVO> getCarDepositByCarNo(@RequestParam("carNo") String carNo);


    @RequestMapping(value = "carDeposit/getById",method = RequestMethod.GET)
    public ResponseData<CarDepositVO> getCarDepositById(@RequestParam("id") Integer id);

    @RequestMapping(value = "carDeposit/update",method = RequestMethod.POST)
    public ResponseData updateCarDeposit(@Valid @RequestBody UpdateCarDepositBillVO updateCarDepositBillVO);
}
