package com.atzuche.order.admin.service;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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

    public ResponseData<CarDepositRespVo> getCarDepositReturnDetail(CarDepositReqVO reqVo) {
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setOrderNo(reqVo.getOrderNo());

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
            throw new RuntimeException("车辆押金获取失败:"+reqVo.getOrderNo());
        }

        AccountRenterDepositDTO accountRenterDepositDTO = data.getAccountRenterDepositDTO();
        RenterDepositDetailDTO renterDepositDetailDTO = data.getRenterDepositDetailDTO();
        OrderDTO orderDTO = data.getOrderDTO();
        AccountRenterDetainCostDTO accountRenterDetainCostDTO = data.getAccountRenterDetainCostDTO();
        List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList = data.getAccountRenterDetainDetailDTOList();
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOS = data.getAccountRenterCostDetailDTOS();
       // List<AccountRenterDepositDetailDTO> accountRenterDepositDetailDTOList = data.getAccountRenterDepositDetailDTOList();
        List<AccountDebtReceivableaDetailDTO> accountDebtReceivableaDetailDTOS = data.getAccountDebtReceivableaDetailDTOS();


        List<AccountRenterDetainDetailDTO> rentCarAmtDtoList = accountRenterDetainDetailDTOList.stream()
                .filter(x -> RenterCashCodeEnum.ACCOUNT_RENTER_DETAIN_CAR_AMT.equals(x.getSourceCode()))
                .collect(Collectors.toList());
        AccountRenterDetainDetailDTO accountRenterDetainDetailDTO = new AccountRenterDetainDetailDTO(0);
        if(rentCarAmtDtoList!= null && rentCarAmtDtoList.size()>=1){
            accountRenterDetainDetailDTO = rentCarAmtDtoList.get(0);
        }

        Integer depositToCarAmt = Optional.ofNullable(accountRenterCostDetailDTOS).orElseGet(ArrayList::new).stream()
                .filter(x -> RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST.equals(x.getSourceCode()))
                .collect(Collectors.summingInt(AccountRenterCostDetailDTO::getAmt));

        /*Integer depositToHistoryAmt = Optional.ofNullable(accountRenterDepositDetailDTOList).orElseGet(ArrayList::new).stream()
                .filter(x -> RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.equals(x.getSourceCode()))
                .collect(Collectors.summingInt(AccountRenterDepositDetailDTO::getAmt));*/
        Integer depositToHistoryAmt = Optional.ofNullable(accountDebtReceivableaDetailDTOS).orElseGet(ArrayList::new).stream()
                .collect(Collectors.summingInt(AccountDebtReceivableaDetailDTO::getAmt));

        String depositType = "";
        String payType = "";
        if(accountRenterDepositDTO.getAuthorizeDepositAmt() != null && accountRenterDepositDTO.getAuthorizeDepositAmt()!= 0){
            depositType = "预授权";
            payType = "支付宝预授权支付";
        }else if(accountRenterDepositDTO.getCreditPayAmt() != null && accountRenterDepositDTO.getCreditPayAmt() != 0){
            depositType = "支付宝信用支付";
        }else{
            depositType = "消费";
            payType = "支付宝/微信";
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

        CarDepositRespVo carDepositRespVo = new CarDepositRespVo();
        carDepositRespVo.setCarDepositMonty(renterDepositDetailDTO.getOriginalDepositAmt());
        carDepositRespVo.setOriginalTotalAmt(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setReceivableMonty(accountRenterDepositDTO.getYingfuDepositAmt());

        carDepositRespVo.setPayType(payType);
        carDepositRespVo.setDepositType(depositType);
        carDepositRespVo.setReliefAmtStr(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setPayDateStr(payTimeStr);
        carDepositRespVo.setPayStatusStr(TransStatusEnum.getFlagText(payStatus));

        carDepositRespVo.setSurplusDepositAmt(accountRenterDepositDTO.getSurplusDepositAmt());
        carDepositRespVo.setRealDeductionRentCarAmt(depositToCarAmt);
        carDepositRespVo.setExpDeductionRentCarAmt(0);
        carDepositRespVo.setDeductionHistoryAmt(depositToHistoryAmt==null?0:depositToHistoryAmt);
        carDepositRespVo.setExpSettleTime(expSettleTime);
        carDepositRespVo.setActSettleTime(actSettleTime);
        carDepositRespVo.setActDetainAmt(accountRenterDetainCostDTO==null?0:accountRenterDetainCostDTO.getAmt());
        carDepositRespVo.setActDetainStatus("成功");
        carDepositRespVo.setActDetainTime(detainTime);

        return ResponseData.success(carDepositRespVo);
    }
}
