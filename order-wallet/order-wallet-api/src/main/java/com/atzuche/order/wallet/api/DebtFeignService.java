package com.atzuche.order.wallet.api;

import com.atzuche.order.commons.entity.dto.MemberDebtListReqDTO;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
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
    
    
    /**
     * 返回用户名下的欠款(区分历史欠款和订单欠款)
     * @param memNo
     * @return ResponseData<DebtDetailVO>
     */
    @RequestMapping(value = "debt/detail",method = RequestMethod.GET)
    public ResponseData<DebtDetailVO> getDebtDetailVO(@RequestParam("memNo")String memNo);

    /**
     * 返回有欠款的用户
     * @param req
     * @return
     */
    @RequestMapping(value = "debt/queryNoList",method = RequestMethod.GET)
    public ResponseData<Page> queryList(MemberDebtListReqDTO req);
}
