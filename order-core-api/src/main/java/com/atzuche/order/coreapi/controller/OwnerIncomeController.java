package com.atzuche.order.coreapi.controller;

import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.vo.res.account.income.AccountOwnerIncomeRealResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


/**
 * 车主收益
 */
@RestController
@RequestMapping("owner/income")
@Slf4j
public class OwnerIncomeController {

	@Autowired
	private CashierQueryService cashierQueryService;


    /**
     * 查询车主收益信息
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "查询车主收益信息", description = "查询车主收益信息", response = AccountOwnerIncomeRealResVO.class)
    @GetMapping("/getOwnerIncomeByOrder")
    public ResponseData<AccountOwnerIncomeRealResVO> getOwnerIncomeByOrder(@RequestParam("orderNo") String orderNo,@RequestParam("memNo") String memNo) {
        log.info("OwnerIncomeController getOwnerIncomeByOrder start param [{}]", orderNo);
        Assert.notNull(orderNo,"主单号不能为空");
        Assert.notNull(memNo,"主单号不能为空");
        AccountOwnerIncomeRealResVO resVO = cashierQueryService.getOwnerRealIncomeByOrder(orderNo,memNo);
        log.info("OwnerIncomeController getOrderPayableAmount end param [{}],result [{}]",orderNo,GsonUtils.toJson(resVO));
        return ResponseData.success(resVO);
    }

}
