package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.res.account.income.AccountOwnerIncomeRealResVO;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 查询车主收益信息
 **/
@FeignClient(name="order-center-api")
public interface FeignOwnerIncomeService {

    /**
     * 查询车主收益信息
     * @param orderNo
     * @return
     */
    @GetMapping("/owner/income/getOwnerIncomeByOrder")
    public ResponseData<AccountOwnerIncomeRealResVO> getOwnerIncomeByOrder(@RequestParam("orderNo") String orderNo);
}
