package com.atzuche.order.coreapi.controller.console;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.coreapi.service.OrderConsoleCostHandleService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
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

    @Resource
    private OrderConsoleCostHandleService orderConsoleCostHandleService;


    @AutoDocMethod(description = "获取订单车辆押金暂扣扣款明细接口", value = "获取订单车辆押金暂扣扣款明细接口", response = GetTempCarDepositInfoResVO.class)
    @PostMapping("/order/temp/get/deposit")
    public ResponseData<GetTempCarDepositInfoResVO> getTempCarDepoists(@Valid @RequestBody GetTempCarDepositInfoReqVO reqVO,
                                           BindingResult bindingResult) {
        log.info("Get order temp car depoist info.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        GetTempCarDepositInfoResVO resVO = orderConsoleCostHandleService.getTempCarDepoistInfos(reqVO.getOrderNo(),
                reqVO.getMemNo(), OrderConstant.CASHNOS);
        return ResponseData.success(resVO);
    }

    @AutoDocMethod(description = "保存订单车辆押金暂扣扣款信息接口", value = "保存订单车辆押金暂扣扣款信息接口")
    @PostMapping("/order/temp/save/depoist")
    public ResponseData saveTempCarDepoist(@Valid @RequestBody SaveTempCarDepositInfoReqVO reqVO, BindingResult bindingResult) {
        log.info("Save order temp car depoist info.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        orderConsoleCostHandleService.saveTempCarDeposit(reqVO);
        return ResponseData.success();
    }

}
