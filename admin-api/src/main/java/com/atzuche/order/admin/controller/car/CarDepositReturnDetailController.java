package com.atzuche.order.admin.controller.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.CarDepositReturnDetailService;
import com.atzuche.order.admin.service.RenterWzService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.req.renterWz.CarDepositTemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@RestController
@AutoDocVersion(version = "车辆押金信息")
public class CarDepositReturnDetailController {

    @Autowired
    private CarDepositReturnDetailService carDepositReturnDetailService;
    @Resource
    private RenterWzService renterWzService;
    @Resource
    private AdminLogService adminLogService;
    @Autowired
    private OrderStatusService orderStatusService;

    @AutoDocMethod(description = "【liujun】车辆押金信息", value = "车辆押金信息", response = CarDepositRespVo.class)
    @PostMapping(value = "/console/deposit/getCarDepositReturnDetail")
    public ResponseData<CarDepositRespVo> getCarDepositReturnDetail(@Valid @RequestBody CarDepositReqVO reqVo, BindingResult bindingResult) {
        log.info("车辆押金信息-reqVo={}", JSON.toJSONString(reqVo));
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        return carDepositReturnDetailService.getCarDepositReturnDetail(reqVo);
    }

    /*@AutoDocMethod(description = "【liujun】车辆押金暂扣处理", value = "车辆押金暂扣处理", response = CarDepositOtherRespVO.class)
    @GetMapping(value = "/console/deposit/return/detail/otherInfo")
    public ResponseData <?> getCarDepositReturnDetailOtherInfo(@Valid CarDepositOtherReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】车辆押金返还处理列表", value = "车辆押金返还处理列表", response = CarDepositReturnDetailResVO.class)
    @GetMapping(value = "/console/deposit/return/detail/list")
    public ResponseData <?> getCarDepositReturnDetail(@Valid CarDepositReturnDetailListReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】保存车辆押金返还处理", value = "保存车辆押金返还处理", response = ResponseData.class)
    @GetMapping(value = "/console/deposit/return/detail/save")
    public ResponseData <?> saveCarDepositReturnDetail(@Valid CarDepositReturnDetailResVO reqVo, BindingResult bindingResult) {

        return null;
    }*/

    @AutoDocMethod(description = "暂扣/取消暂扣租车押金", value = "暂扣/取消暂扣租车押金",response = ResponseData.class)
    @PostMapping("/console/save/carDeposit/temporaryRefund")
    public ResponseData saveCarDepositTemporaryRefund(@Valid @RequestBody CarDepositTemporaryRefundReqVO req, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(req.getOrderNo());
        if(SettleStatusEnum.SETTLEING.getCode() == orderStatusEntity.getCarDepositSettleStatus()){
            log.error("已经结算不允许编辑orderNo={}",req.getOrderNo());
            throw new NotAllowedEditException();
        }

        renterWzService.saveCarDepositTemporaryRefund(req);
        try{
            adminLogService.insertLog(AdminOpTypeEnum.TEMPORARY_WZ_REFUND,req.getOrderNo(),req.toString());
        }catch (Exception e){
            log.warn("暂扣租车押金日志记录失败",e);
        }
        return ResponseData.success();
    }
}
