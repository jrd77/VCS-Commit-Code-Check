package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.atzuche.order.commons.exceptions.CleanRefoundException;
import com.atzuche.order.commons.vo.req.ClearingRefundReqVO;
import com.autoyol.autopay.gateway.api.AutoPayGatewaySecondaryService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.vo.Response;
import com.autoyol.autopay.gateway.vo.req.PreRoutingPayRequest;
import com.autoyol.autopay.gateway.vo.req.QueryVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class ClearingRefundService {

    @Autowired
    private CashierRefundApplyMapper cashierRefundApplyMapper;
    @Autowired
    private AutoPayGatewaySecondaryService autoPayGatewaySecondaryService;
    @Autowired
    private CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired
    private CashierPayService cashierPayService;

    public Response<AutoPayResultVo> clearingRefundToPerformance(CashierEntity cashierEntity) {
        PreRoutingPayRequest preRoutingPayRequest = new PreRoutingPayRequest();
        preRoutingPayRequest.setPayTransId(cashierEntity.getPayTransNo());
        preRoutingPayRequest.setSourceOs("PC");
        preRoutingPayRequest.setSourceIp("localhost");
        preRoutingPayRequest.setInternalNo(cashierEntity.getVersion()==null?"0":String.valueOf(cashierEntity.getVersion()));
        preRoutingPayRequest.setEnv(cashierEntity.getPayEvn());
        preRoutingPayRequest.setPayAmt(cashierEntity.getPayAmt()==null?"0":String.valueOf(cashierEntity.getPayAmt())); //跟金额无关。
        Response<AutoPayResultVo> autoPayResultVoResponse = routingRulesQuery(preRoutingPayRequest);
        return autoPayResultVoResponse;
    }
    public Response<AutoPayResultVo> clearingRefundToQuery(CashierEntity cashierEntity){
        QueryVo queryVo = new QueryVo();
        queryVo.setAtappId(cashierEntity.getAtappId());
        queryVo.setPayEnv(cashierEntity.getPayEvn());
        queryVo.setPayId(String.valueOf(cashierEntity.getId()));
        queryVo.setPayKind(cashierEntity.getPayKind());
        queryVo.setPayType(cashierEntity.getPayType());
        queryVo.setMemNo(cashierEntity.getMemNo());
        queryVo.setOrderNo(cashierEntity.getOrderNo());
        queryVo.setPaySn(cashierEntity.getQn());
        queryVo.setPaySource(cashierEntity.getPaySource());
        queryVo.setInternalNo(cashierEntity.getVersion()==null?"0":String.valueOf(cashierEntity.getVersion()));
        queryVo.setPayTime(cashierEntity.getPayTime());
        queryVo.setAtpayNewTransId(cashierEntity.getPayTransNo());
        Response<AutoPayResultVo> autoPayResultVoResponse = routingRulesQuery(queryVo);
        return autoPayResultVoResponse;
    }



    public Integer clearingRefundSubmitToRefund(ClearingRefundReqVO clearingRefundReqVO,CashierEntity cashierEntity) {
        String payType = cashierEntity.getPayType();
        String payTypeReq = clearingRefundReqVO.getPayType();
        if(payTypeReq==null
                || (DataPayTypeConstant.PAY_PRE.equals(payType) && !Arrays.asList("03","32").contains(payTypeReq))
                || DataPayTypeConstant.PAY_PUR.equals(payType) && !Arrays.asList("04").contains(payTypeReq)){
            CleanRefoundException e = new CleanRefoundException("操作类型与流水记录不匹配");
            log.error("清算退款-操作类型与流水记录不匹配clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        if(clearingRefundReqVO.getAmt()==null || clearingRefundReqVO.getAmt()<=0){
            CleanRefoundException e = new CleanRefoundException("退款金额不能小于等于0");
            log.error("清算退款-退款金额不能小于等于0clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        if(cashierEntity.getPayAmt()==null || cashierEntity.getPayAmt() < clearingRefundReqVO.getAmt()){
            CleanRefoundException e = new CleanRefoundException("退款金额大于流水金额，不予退款");
            log.error("清算退款-退款金额大于流水金额clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        //保存记录
        RenterCashCodeEnum renterCashCodeEnum = getCashCodeByPayKind(cashierEntity.getPayKind());
        CashierRefundApplyEntity cashierRefundApplyEntity = new CashierRefundApplyEntity();
        cashierRefundApplyEntity.setMemNo(cashierEntity.getMemNo());
        cashierRefundApplyEntity.setOrderNo(clearingRefundReqVO.getOrderNo());
        cashierRefundApplyEntity.setAtappId(cashierEntity.getAtappId());
        cashierRefundApplyEntity.setPayKind(cashierEntity.getPayKind());
        cashierRefundApplyEntity.setPayType(clearingRefundReqVO.getPayType());
        cashierRefundApplyEntity.setPaySource(cashierEntity.getPaySource());
        cashierRefundApplyEntity.setQn(cashierEntity.getQn());
        cashierRefundApplyEntity.setUniqueNo(null);
        cashierRefundApplyEntity.setType(0);
        cashierRefundApplyEntity.setNum(1);
        cashierRefundApplyEntity.setPayMd5(cashierEntity.getPayMd5());
        cashierRefundApplyEntity.setSourceCode(renterCashCodeEnum.getCashNo());
        cashierRefundApplyEntity.setSourceDetail(renterCashCodeEnum.getTxt());
        cashierRefundApplyEntity.setAmt(clearingRefundReqVO.getAmt());
        cashierRefundApplyEntity.setStatus(CashierRefundApplyStatus.WAITING_FOR_REFUND.getCode());
        cashierRefundApplyEntity.setFlag(null);
        int result = cashierRefundApplyMapper.insertSelective(cashierRefundApplyEntity);
        log.info("清算退款-记录保存result={},参数cashierRefundApplyEntity={}",result,JSON.toJSONString(cashierRefundApplyEntity));

        //立即退款操作
        CashierRefundApplyEntity cashierRefundApply = cashierRefundApplyNoTService.selectorderNo(cashierEntity.getOrderNo(),cashierEntity.getPayKind());
        cashierPayService.refundOrderPay(cashierRefundApply);

        return result;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/6/3 16:14
     * @Description: 更具payKind选择使用哪一个费用编码
     *
     **/
    private RenterCashCodeEnum getCashCodeByPayKind(String payKind){
        if(StringUtils.isBlank(payKind)){
            CleanRefoundException e = new CleanRefoundException("paykind为空");
            log.error("清算退款-操作类型与流水记录不匹配payKind={}",payKind,e);
            throw e;
        }
        //违章押金
        if(DataPayKindConstant.DEPOSIT.equals(payKind)){
            return RenterCashCodeEnum.CLEAR_REFOUND_WZ_DEPOSIT;
        }else if(DataPayKindConstant.RENT.equals(payKind)){
            return  RenterCashCodeEnum.CLEAR_REFOUND_DEPOSIT;
        }else if(DataPayKindConstant.RENT_AMOUNT.equals(payKind)){
            return  RenterCashCodeEnum.CLEAR_REFOUND_RENT;
        }else{
           CleanRefoundException e = new CleanRefoundException("payKind找不到对应的费用编码");
            log.error("payKind找不到对应的费用编码payKind={}",payKind,e);
            throw e;
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/6/3 17:54
     * @Description: 清算退款远程调用pay服务查询
     *
     **/
    public Response<AutoPayResultVo> routingRulesQuery(QueryVo queryVo){
        Response<AutoPayResultVo> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "清算退款远程调用pay服务查询");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"autoPayGatewaySecondaryService.routingRulesQuery");
            log.info("Feign 清算退款远程调用pay服务查询,queryVo={}", JSON.toJSONString(queryVo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(queryVo));
            responseObject =  autoPayGatewaySecondaryService.routingRulesQuery(queryVo);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(queryVo));
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 清算退款远程调用pay服务查询异常,responseObject={},queryVo={}", JSON.toJSONString(responseObject),JSON.toJSONString(queryVo),e);
            Cat.logError("Feign 清算退款远程调用pay服务查询异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/6/3 17:54
     * @Description: 清算退款远程调用pay服务查询
     *
     **/
    public Response<AutoPayResultVo> routingRulesQuery(PreRoutingPayRequest preRoutingPayRequest){
        Response<AutoPayResultVo> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "清算退款-支付宝同步履约远程调用pay服务查询");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"autoPayGatewaySecondaryService.routingRulesQuery");
            log.info("Feign 清算退款-支付宝同步履约远程调用pay服务查询,preRoutingPayRequest={}", JSON.toJSONString(preRoutingPayRequest));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(preRoutingPayRequest));
            responseObject =  autoPayGatewaySecondaryService.routingRulesPaySyncZhima(preRoutingPayRequest);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(preRoutingPayRequest));
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 清算退款-支付宝同步履约远程调用pay服务查询异常,responseObject={},preRoutingPayRequest={}", JSON.toJSONString(responseObject),JSON.toJSONString(preRoutingPayRequest),e);
            Cat.logError("Feign 清算退款-支付宝同步履约远程调用pay服务查询异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
