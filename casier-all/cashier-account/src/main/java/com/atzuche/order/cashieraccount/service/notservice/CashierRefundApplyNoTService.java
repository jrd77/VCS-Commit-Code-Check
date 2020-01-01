package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.CashierRefundApplyException;
import com.atzuche.order.cashieraccount.exception.OrderPayRefundCallBackAsnyException;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;

import java.util.Objects;


/**
 * 退款申请表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierRefundApplyNoTService {
    @Autowired
    private CashierRefundApplyMapper cashierRefundApplyMapper;


    /**
     * 记录待退款信息
     * @param cashierRefundApplyReq
     */
    public int insertRefundDeposit(CashierRefundApplyReqVO cashierRefundApplyReq) {
        CashierRefundApplyEntity cashierRefundApplyEntity = new CashierRefundApplyEntity();
        BeanUtils.copyProperties(cashierRefundApplyReq,cashierRefundApplyEntity);
        cashierRefundApplyEntity.setStatus(CashierRefundApplyStatus.WAITING_FOR_REFUND.getCode());
        cashierRefundApplyEntity.setSourceCode(cashierRefundApplyReq.getRenterCashCodeEnum().getCashNo());
        cashierRefundApplyEntity.setSourceDetail(cashierRefundApplyReq.getRenterCashCodeEnum().getTxt());
        int result = cashierRefundApplyMapper.insert(cashierRefundApplyEntity);
        if(result==0){
            throw new CashierRefundApplyException();
        }
        return cashierRefundApplyEntity.getId();
    }


    /**
     * 退款回调信息
     */
    public void updateRefundDepositSuccess(NotifyDataVo notifyDataVo) {
        //1 校验
        CashierRefundApplyEntity cashierRefundApplyEntity = cashierRefundApplyMapper.selectRefundByQn(notifyDataVo.getMemNo(),notifyDataVo.getOrderNo(),notifyDataVo.getQn());
        //2 回调退款是否成功判断 TODOD
        if(Objects.nonNull(cashierRefundApplyEntity) && CashierRefundApplyStatus.RECEIVED_REFUND.getCode().equals(notifyDataVo.getTransStatus())){
            //3 更新退款成功
            CashierRefundApplyEntity cashierRefundApplyUpdate = new CashierRefundApplyEntity();
            cashierRefundApplyUpdate.setStatus(CashierRefundApplyStatus.RECEIVED_REFUND.getCode());
            cashierRefundApplyUpdate.setVersion(cashierRefundApplyEntity.getVersion());
            cashierRefundApplyUpdate.setId(cashierRefundApplyEntity.getId());
            int result = cashierRefundApplyMapper.updateByPrimaryKeySelective(cashierRefundApplyUpdate);
            if(result==0){
                throw new OrderPayRefundCallBackAsnyException();
            }
            //4 成功之后push 或者 短信
        }
    }

    /**
     * 更新发起退款次数
     * @param cashierRefundApply
     */
    public void updateCashierRefundApplyEntity(CashierRefundApplyEntity cashierRefundApply) {
        CashierRefundApplyEntity cashierRefundApplyEntity = new CashierRefundApplyEntity();
        cashierRefundApplyEntity.setId(cashierRefundApply.getId());
        cashierRefundApplyEntity.setNum(cashierRefundApply.getNum()+1);
        cashierRefundApplyEntity.setVersion(cashierRefundApply.getVersion());
        cashierRefundApplyMapper.updateByPrimaryKeySelective(cashierRefundApplyEntity);
    }
}
