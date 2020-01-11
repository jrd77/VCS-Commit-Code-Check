package com.atzuche.order.wallet.api;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 3:41 下午
 **/
@FeignClient(name="order-wallet-service")
public interface OrderWalletFeignService {

    /**
     * 返回用户名下钱包的总额
     * @param memNo
     * @return
     */
    @RequestMapping(value = "wallet/get",method = RequestMethod.GET)
    ResponseData<TotalWalletVO> getWalletTotalByMemNo(@RequestParam("memNo") String memNo);


    /**
     * 订单扣减用户的钱包
     * @param deductWalletVO
     * @return
     */
    @RequestMapping(value = "wallet/deduct",method = RequestMethod.POST)
    public ResponseData<DeductWalletVO> deductWallet(@RequestBody DeductWalletVO deductWalletVO);

    /**
     * 订单结算退还用户的钱包
     * @param deductWalletVO
     * @return
     */
    @RequestMapping(value = "wallet/ret",method = RequestMethod.POST)
    public ResponseData returnOrChangeWallet(@RequestBody DeductWalletVO deductWalletVO);
}
