package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.coreapi.service.OrderConsoleCostHandleService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 管理后台维护订单相关费用(管理后台添加的费用)操作集合
 *
 * @author pengcheng.fu
 * @date 2020/4/23 16:49
 */

@RestController
@AutoDocVersion(version = "管理后台订单费用维护接口文档")
@Slf4j
public class OrderConsoleCostHandleController {

    @Autowired
    private OrderConsoleCostHandleService orderConsoleCostHandleService;


    @AutoDocMethod(description = "获取订单暂扣车辆押金费用明细接口", value = "获取订单暂扣车辆押金费用明细接口")
    @PostMapping("/order/temp/depoist/info")
    public ResponseData getTempCarDepoists(@Valid @RequestBody GetTempCarDepositInfoReqVO reqVO,
                                           BindingResult bindingResult) {
        log.info("Get order temp car depoist info.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        List<String> cashNos = new ArrayList<>(
                Arrays.asList(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_FINE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_STOPCHARGE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_REPAIRCHARGE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_STOPCHARGE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_REPAIRCHARGE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_STOPCHARGE.getCashNo(),
                        ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_COLLECTCHARGE.getCashNo()
                ));
        GetTempCarDepositInfoResVO resVO = orderConsoleCostHandleService.getTempCarDepoistInfos(reqVO.getOrderNo(),
                reqVO.getMemNo(), cashNos);
        return ResponseData.success(resVO);
    }


}
