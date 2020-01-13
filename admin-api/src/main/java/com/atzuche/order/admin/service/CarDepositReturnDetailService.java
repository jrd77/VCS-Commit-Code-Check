package com.atzuche.order.admin.service;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.exception.CarDepositQueryException;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.car.RenterCarDetailErrException;
import com.atzuche.order.car.RenterCarDetailFailException;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                log.error("Feign 获取费用详情失败,responseData={},orderDetailReqDTO={}",JSON.toJSONString(responseData),JSON.toJSONString(responseData));
                RenterCarDetailFailException failException = new RenterCarDetailFailException();
                Cat.logError("Feign 获取费用详情失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 获取费用详情失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 获取费用详情异常,responseData={},orderDetailReqDTO={}",JSON.toJSONString(responseData),JSON.toJSONString(responseData),e);
            RenterCarDetailErrException carDetailByFeignException = new RenterCarDetailErrException();
            Cat.logError("Feign 获取费用详情异常",carDetailByFeignException);
            throw carDetailByFeignException;
        }finally {
            t.complete();
        }
        OrderAccountDetailRespDTO data = responseData.getData();
        OrderStatusDTO orderStatusDTO = data.orderStatusDTO;
        if(orderStatusDTO == null){
            CarDepositQueryException carDepositQueryException = new CarDepositQueryException();
            log.error("车辆押金获取失败",carDepositQueryException);
            throw carDepositQueryException;
        }

        AccountRenterDepositDTO accountRenterDepositDTO = data.getAccountRenterDepositDTO();
        RenterDepositDetailDTO renterDepositDetailDTO = data.getRenterDepositDetailDTO();
        AccountRenterDetainCostDTO accountRenterDetainCostDTO = data.getAccountRenterDetainCostDTO();
        List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList = data.getAccountRenterDetainDetailDTOList();
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOS = data.getAccountRenterCostDetailDTOS();
        List<AccountRenterDepositDetailDTO> accountRenterDepositDetailDTOList = data.getAccountRenterDepositDetailDTOList();


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

        Integer depositToHistoryAmt = accountRenterDepositDetailDTOList.stream()
                .filter(x -> RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.equals(x.getSourceCode()))
                .collect(Collectors.summingInt(AccountRenterDepositDetailDTO::getAmt));


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
        int payStatus = Integer.valueOf(accountRenterDepositDTO.getPayStatus());
        LocalDateTime actSettleTimeLocalDateTIme = orderStatusDTO.getSettleTime();
        String actSettleTime = LocalDateTimeUtils.formateLocalDateTimeStr(actSettleTimeLocalDateTIme, GlobalConstant.DATE_TIME_FORMAT_1);
        LocalDateTime expSettleTimeLocalDateTIme = orderStatusDTO.getSettleTime().plusHours(4);
        String expSettleTime = LocalDateTimeUtils.formateLocalDateTimeStr(expSettleTimeLocalDateTIme, GlobalConstant.DATE_TIME_FORMAT_1);
        LocalDateTime time = accountRenterDetainDetailDTO.getTime();
        String detainTime = LocalDateTimeUtils.formateLocalDateTimeStr(time, GlobalConstant.DATE_TIME_FORMAT_1);

        CarDepositRespVo carDepositRespVo = new CarDepositRespVo();
        carDepositRespVo.setCarDepositMonty(renterDepositDetailDTO.getOriginalDepositAmt());
        carDepositRespVo.setOriginalTotalAmt(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setReceivableMonty(accountRenterDepositDTO.getYingfuDepositAmt());


        carDepositRespVo.setPayType(payType);
        carDepositRespVo.setDepositType(depositType);
        carDepositRespVo.setReliefAmtStr(renterDepositDetailDTO.getReductionDepositAmt());
        carDepositRespVo.setPayDateStr(LocalDateTimeUtils.formateLocalDateTimeStr(accountRenterDepositDTO.getPayTime(), GlobalConstant.DATE_TIME_FORMAT_1));
        carDepositRespVo.setPayStatusStr(OrderPayStatusEnum.from(payStatus).getDesc());

        carDepositRespVo.setSurplusDepositAmt(accountRenterDepositDTO.getSurplusDepositAmt());
        carDepositRespVo.setRealDeductionRentCarAmt(depositToCarAmt);
        carDepositRespVo.setExpDeductionRentCarAmt(0);
        carDepositRespVo.setDeductionHistoryAmt(depositToHistoryAmt);
        carDepositRespVo.setExpSettleTime(expSettleTime);
        carDepositRespVo.setActSettleTime(actSettleTime);
        carDepositRespVo.setActDetainAmt(accountRenterDetainCostDTO.getAmt());
        carDepositRespVo.setActDetainStatus("成功");
        carDepositRespVo.setActDetainTime(detainTime);

        return ResponseData.success(carDepositRespVo);
    }
}
