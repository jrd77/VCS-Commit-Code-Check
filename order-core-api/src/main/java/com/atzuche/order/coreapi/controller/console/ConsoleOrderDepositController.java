package com.atzuche.order.coreapi.controller.console;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.vo.detain.CarDepositDetainReqVO;
import com.atzuche.order.commons.vo.detain.IllegalDepositDetainReqVO;
import com.atzuche.order.coreapi.service.console.ConsoleOrderDepositHandleService;
import com.atzuche.order.detain.dto.CarDepositTemporaryRefundReqDTO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 管理后台订单押金相关处理集合
 *
 * @author pengcheng.fu
 * @date 2020/4/28 11:57
 */

@RestController
@AutoDocVersion(version = "订单押金相关接口文档")
@Slf4j
public class ConsoleOrderDepositController {

    @Resource
    private ConsoleOrderDepositHandleService consoleOrderDepositHandleService;


    @PostMapping("/order/wz/deposit/detain/save")
    @AutoDocMethod(description = "订单违章押金暂扣/撤销暂扣接口", value = "订单违章押金暂扣/撤销暂扣接口")
    public ResponseData<?> illegalDepositDetain(@Valid @RequestBody IllegalDepositDetainReqVO reqVO,
                                                BindingResult bindingResult) {
        log.info("Illegal deposit amt detain/undo.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        if (reqVO.getDetainStatus() == OrderConstant.ONE) {
            consoleOrderDepositHandleService.wzDepositDetainHandle(reqVO.getOrderNo());
        } else if (reqVO.getDetainStatus() == OrderConstant.TWO) {
            consoleOrderDepositHandleService.wzDepositUndoDetainHandle(reqVO.getOrderNo());
        } else {
            throw new InputErrorException("detainStatus(违章押金暂扣状态)传入的值无效.");
        }
        return ResponseData.success();
    }


    @PostMapping("/order/car/deposit/detain/save")
    @AutoDocMethod(description = "订单车辆押金暂扣/撤销暂扣接口", value = "订单车辆押金暂扣/撤销暂扣接口")
    public ResponseData<?> carDepositDetain(@Valid @RequestBody CarDepositDetainReqVO reqVO,
                                            BindingResult bindingResult) {
        log.info("Car deposit amt detain/undo.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund = new CarDepositTemporaryRefundReqDTO();
        BeanUtils.copyProperties(reqVO, carDepositTemporaryRefund);
        if (reqVO.getDetainStatus() == OrderConstant.ONE) {
            consoleOrderDepositHandleService.carDepositDetainHandle(carDepositTemporaryRefund);
        } else if (reqVO.getDetainStatus() == OrderConstant.TWO) {
            consoleOrderDepositHandleService.carDepositUndoDetainHandle(carDepositTemporaryRefund);
        } else {
            throw new InputErrorException("detainStatus(车辆押金暂扣状态)传入的值无效.");
        }
        return ResponseData.success();
    }

}
