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

    /**
     * 根据account的id查询绑卡信息
     * @param id
     * @return
     */
    @RequestMapping(value = "account/id",method = RequestMethod.GET)
    public ResponseData<AccountVO> findAccountByMemNo(@RequestParam("id")Integer id);

    /**
     * 扣减用户的提现余额
     * @param deductBalanceVO
     * @return
     */
    @RequestMapping(value = "balance/deduct",method = RequestMethod.POST)
    public ResponseData deductBalance(@Valid @RequestBody DeductBalanceVO deductBalanceVO);

    /**
     * 查询用户列表中每个用户的绑卡数量
     * @param reqVO
     * @return
     */
    @RequestMapping(value = "account/stat",method = RequestMethod.POST)
    public ResponseData<MemAccountStatRespVO> statMemAccount(@Valid @RequestBody MemAccountStatReqVO reqVO);

    /**
     * 获取用户可提现余额
     * @param memNo
     * @return
     */
    @RequestMapping(value = "balance/get",method = RequestMethod.GET)
    public ResponseData<MemBalanceVO> getMemBalance(@RequestParam("memNo")String memNo);
}
