package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.exceptions.CleanRefoundException;
import com.atzuche.order.commons.exceptions.NotFoundCashierException;
import com.atzuche.order.coreapi.entity.request.ClearingRefundReqVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClearingRefundService {
    @Autowired
    private CashierNoTService cashierNoTService;
    @Autowired
    private CashierRefundApplyMapper cashierRefundApplyMapper;

    public void clearingRefundSubmitToQuery(ClearingRefundReqVO clearingRefundReqVO){




    }
    public Integer clearingRefundSubmitToRefund(ClearingRefundReqVO clearingRefundReqVO,CashierEntity cashierEntity) {
        String payType = cashierEntity.getPayType();
        String payTypeReq = clearingRefundReqVO.getPayType();
        if(payTypeReq==null || !payTypeReq.equals(payType)){
            CleanRefoundException e = new CleanRefoundException("操作类型与流水记录不匹配");
            log.error("清算退款-操作类型与流水记录不匹配clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        String paySource = cashierEntity.getPaySource();
        String paySourcereq = clearingRefundReqVO.getPaySource();
        if(paySourcereq==null || !paySourcereq.equals(paySource)){
            CleanRefoundException e = new CleanRefoundException("支付来源与流水记录不匹配");
            log.error("清算退款-操作类型与流水记录不匹配clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        //TODO 金额不为空的情况下的校验

        //保存记录
        RenterCashCodeEnum renterCashCodeEnum = getCashCodeByPayKind(cashierEntity.getPayKind());
        CashierRefundApplyEntity cashierRefundApplyEntity = new CashierRefundApplyEntity();
        cashierRefundApplyEntity.setMemNo(cashierEntity.getMemNo());
        cashierRefundApplyEntity.setOrderNo(clearingRefundReqVO.getOrderNo());
        cashierRefundApplyEntity.setAtappId(cashierEntity.getAtappId());
        cashierRefundApplyEntity.setPayKind(cashierEntity.getPayKind());
        cashierRefundApplyEntity.setPayType(clearingRefundReqVO.getPayType());
        cashierRefundApplyEntity.setPaySource(paySourcereq);
        cashierRefundApplyEntity.setQn(cashierEntity.getQn());
        cashierRefundApplyEntity.setUniqueNo(null);
        cashierRefundApplyEntity.setType(0);
        cashierRefundApplyEntity.setNum(0);
        cashierRefundApplyEntity.setPayMd5(cashierEntity.getPayMd5());
        cashierRefundApplyEntity.setSourceCode(renterCashCodeEnum.getCashNo());
        cashierRefundApplyEntity.setSourceDetail(renterCashCodeEnum.getTxt());
        cashierRefundApplyEntity.setAmt(clearingRefundReqVO.getAmt());
        cashierRefundApplyEntity.setStatus(String.valueOf(OrderRefundStatusEnum.REFUNDING.getStatus()));
        cashierRefundApplyEntity.setFlag(null);
        int result = cashierRefundApplyMapper.insertSelective(cashierRefundApplyEntity);
        log.info("清算退款-记录保存result={},参数cashierRefundApplyEntity={}",result,JSON.toJSONString(cashierRefundApplyEntity));
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
}
