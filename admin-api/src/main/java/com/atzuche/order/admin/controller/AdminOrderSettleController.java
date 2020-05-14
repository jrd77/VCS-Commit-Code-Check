package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.open.service.FeignOrderSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("settle")
@Slf4j
public class AdminOrderSettleController {
    @Autowired
    private RemoteFeignService remoteFeignService;

    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "手动车辆结算接口", description = "手动车辆结算接口", response = String.class)
    @GetMapping("/deposit")
    public ResponseData<String> settleDeposit(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleDeposit start param [{}]", orderNo);
        ResponseData result = remoteFeignService.depositSettleFromRemote(orderNo);
        //ResponseData result = feignOrderSettleService.depositSettle(orderNo);
        log.info("CashierController settleDeposit end param [{}],result [{}]",orderNo,result);
        return result;
    }


    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "手动结算违章押金", description = "结算违章押金", response = String.class)
    @GetMapping("/settleOrderWz")
    public ResponseData<String> settleOrderWz(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleOrderWz start param [{}]", orderNo);
        remoteFeignService.settleOrderWzFromRemote(orderNo);
        log.info("OrderSettleController settleOrderWz end param [{}],result [{}]");
        return ResponseData.success();
    }


}
