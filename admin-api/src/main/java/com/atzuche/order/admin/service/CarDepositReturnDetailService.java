package com.atzuche.order.admin.service;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.enums.detain.DetainStatusEnum;
import com.atzuche.order.commons.enums.detain.DetainTypeEnum;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarDepositReturnDetailService {
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;
    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private RenterOrderService renterOrderService;

    public ResponseData<CarDepositRespVo> getCarDepositReturnDetail(CarDepositReqVO reqVo) {
        String orderNo = reqVo.getOrderNo();
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setOrderNo(orderNo);

        ResponseData<OrderAccountDetailRespDTO> responseData = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取费用详情");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FeignOrderDetailService.orderAccountDetail");
            log.info("Feign 开始获取费用详情,orderDetailReqDTO={}", JSON.toJSONString(orderDetailReqDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderDetailReqDTO));
            responseData = feignOrderDetailService.orderAccountDetail(orderDetailReqDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(orderDetailReqDTO));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取费用详情异常,responseData={},orderDetailReqDTO={}",JSON.toJSONString(responseData),JSON.toJSONString(responseData),e);
            Cat.logError("Feign 获取费用详情异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        OrderAccountDetailRespDTO data = responseData.getData();
        OrderStatusDTO orderStatusDTO = data.orderStatusDTO;
        if(orderStatusDTO == null){
            throw new RuntimeException("车辆押金获取失败:"+ orderNo);
        }
        RenterOrderDTO renterOrderDTO = data.getRenterOrderDTO();
        String renterOrderNo = renterOrderDTO.getRenterOrderNo();
        String renterMemNo = renterOrderDTO.getRenterMemNo();
        AccountRenterDepositDTO accountRenterDepositDTO = data.getAccountRenterDepositDTO();
        RenterDepositDetailDTO renterDepositDetailDTO = data.getRenterDepositDetailDTO();
        OrderDTO orderDTO = data.getOrderDTO();
        List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList = data.getAccountRenterDetainDetailDTOList();
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOS = data.getAccountRenterCostDetailDTOS();
        List<AccountDebtReceivableaDetailDTO> accountDebtReceivableaDetailDTOS = data.getAccountDebtReceivableaDetailDTOS();
        CashierDTO cashierDTO = data.getCashierDTO();

        List<AccountRenterDetainDetailDTO> rentCarAmtDtoList = accountRenterDetainDetailDTOList.stream()
                .filter(x -> RenterCashCodeEnum.ACCOUNT_DEPOSIT_DETAIN_CAR_AMT.getCashNo().equals(x.getSourceCode().toString()))
                .collect(Collectors.toList());
        AccountRenterDetainDetailDTO accountRenterDetainDetailDTO = new AccountRenterDetainDetailDTO(0);
        int detainAmt = 0;
        if(!CollectionUtils.isEmpty(rentCarAmtDtoList)){
            accountRenterDetainDetailDTO = rentCarAmtDtoList.get(0);
            detainAmt = rentCarAmtDtoList.stream().mapToInt(AccountRenterDetainDetailDTO::getAmt).sum();
        }

        int depositToCarAmt = Optional.ofNullable(accountRenterCostDetailDTOS).orElseGet(ArrayList::new).stream()
                .filter(x -> RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST.equals(x.getSourceCode()))
                .collect(Collectors.summingInt(x -> x.getAmt() == null ? 0 : x.getAmt()));

        int depositToHistoryAmt = Optional.ofNullable(accountDebtReceivableaDetailDTOS)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.getCashNo().equals(x.getSourceCode()))
                .collect(Collectors.summingInt(x -> x.getAmt() == null ? 0 : x.getAmt()));

        String depositType = "";
        String payType = "";
        if(cashierDTO != null){
            depositType = PaySourceEnum.getFlagText(cashierDTO.getPaySource());
            payType = PayTypeEnum.getFlagText(cashierDTO.getPayType());
        }

        String payStatus = accountRenterDepositDTO.getPayStatus();
        LocalDateTime actSettleTimeLocalDateTIme = orderStatusDTO.getSettleTime();
        String actSettleTime = "";
        if(actSettleTimeLocalDateTIme != null){
            actSettleTime = LocalDateTimeUtils.formateLocalDateTimeStr(actSettleTimeLocalDateTIme, GlobalConstant.DATE_TIME_FORMAT_1);
        }
        LocalDateTime expSettleTimeLocalDateTIme = orderDTO.getExpRevertTime().plusHours(4);
        String expSettleTime = LocalDateTimeUtils.formateLocalDateTimeStr(expSettleTimeLocalDateTIme, GlobalConstant.DATE_TIME_FORMAT_1);
        LocalDateTime time = accountRenterDetainDetailDTO.getTime();
        String detainTime = "";
        if(time != null){
            detainTime = LocalDateTimeUtils.formateLocalDateTimeStr(time, GlobalConstant.DATE_TIME_FORMAT_1);
        }
        LocalDateTime payTime = accountRenterDepositDTO.getPayTime();
        String payTimeStr = "";
        if(payTime != null){
            payTimeStr = LocalDateTimeUtils.formateLocalDateTimeStr(payTime, GlobalConstant.DATE_TIME_FORMAT_1);
        }
        RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
        RenterCostVO renterCostVO = orderSettleService.getRenterCostByOrderNo(orderNo,renterOrderNo,renterMemNo,rentCost.getRenterCostAmtFinal());
        CarDepositRespVo carDepositRespVo = new CarDepositRespVo();
        int expOrActFlag = 1;
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusDTO.getCarDepositSettleStatus()){//车辆已经结算
            expOrActFlag = 2;
            carDepositRespVo.setRealDeductionRentCarAmt(depositToCarAmt);
        }else{
            carDepositRespVo.setExpDeductionRentCarAmt(renterCostVO.getDepositCostYingkou());
        }
        carDepositRespVo.setExpOrActFlag(expOrActFlag);
        carDepositRespVo.setCarDepositMonty(renterDepositDetailDTO.getOriginalDepositAmt());
        carDepositRespVo.setOriginalTotalAmt(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setReceivableMonty(Math.abs(accountRenterDepositDTO.getYingfuDepositAmt()));

        carDepositRespVo.setPayType(payType);
        carDepositRespVo.setDepositType(depositType);
        carDepositRespVo.setReliefAmtStr(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setPayDateStr(payTimeStr);
        carDepositRespVo.setPayStatusStr(TransStatusEnum.getFlagText(payStatus));
        carDepositRespVo.setSurplusDepositAmt(accountRenterDepositDTO.getSurplusDepositAmt());

        carDepositRespVo.setDeductionHistoryAmt(depositToHistoryAmt);
        carDepositRespVo.setExpSettleTime(expSettleTime);
        carDepositRespVo.setActSettleTime(actSettleTime);

        carDepositRespVo.setActDetainAmt(detainAmt);
        DetainStatusEnum detainStatus = DetainStatusEnum.from(orderStatusDTO.getIsDetain());
        if(null == detainStatus) {
            detainStatus = DetainStatusEnum.NO_DETAIN;
        }
        carDepositRespVo.setActDetainStatus(detainStatus.getMsg());
        carDepositRespVo.setActDetainTime(detainTime);

        detainReasonHandle(carDepositRespVo, data.getDetainReasons());

        return ResponseData.success(carDepositRespVo);
    }


    /**
     * 租车押金暂扣原因处理
     *
     * @param res           返回信息
     * @param detainReasons 暂扣原因列表
     */
    private void detainReasonHandle(CarDepositRespVo res, List<RenterDetainReasonDTO> detainReasons) {
        if (!CollectionUtils.isEmpty(detainReasons)) {
            detainReasons.forEach(r -> {
                if (StringUtils.equals(r.getDetainTypeCode(), DetainTypeEnum.risk.getCode())) {
                    res.setFkDetainFlag(r.getDetainStatus().toString());
                    res.setFkDetainReason(r.getDetainReasonCode());
                } else if (StringUtils.equals(r.getDetainTypeCode(), DetainTypeEnum.trans.getCode())) {
                    res.setJyDetainFlag(r.getDetainStatus().toString());
                    res.setJyDetainReason(r.getDetainReasonCode());
                } else if (StringUtils.equals(r.getDetainTypeCode(), DetainTypeEnum.claims.getCode())) {
                    res.setLpDetainFlag(r.getDetainStatus().toString());
                    res.setLpDetainReason(r.getDetainReasonCode());
                }
            });
        }
    }
}
