package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.CashierRefundApplyException;
import com.atzuche.order.cashieraccount.exception.OrderPayRefundCallBackAsnyException;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.doc.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;

import java.time.LocalDateTime;
import java.util.List;
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

    @Value("${refundWatingDays:1}") String refundWatingDays;

    /**
     * 记录待退款信息
     * @param cashierRefundApplyReq
     */
    public int insertRefundDeposit(CashierRefundApplyReqVO cashierRefundApplyReq) {
        CashierRefundApplyEntity cashierRefundApplyEntity = new CashierRefundApplyEntity();
        BeanUtils.copyProperties(cashierRefundApplyReq,cashierRefundApplyEntity);
        if(org.apache.commons.lang.StringUtils.isBlank(cashierRefundApplyReq.getStatus())) {
        	//默认值处理，否则以传过来的数据为准。
        	cashierRefundApplyEntity.setStatus(CashierRefundApplyStatus.WAITING_FOR_REFUND.getCode());
        }
        cashierRefundApplyEntity.setSourceCode(cashierRefundApplyReq.getRenterCashCodeEnum().getCashNo());
        cashierRefundApplyEntity.setSourceDetail(cashierRefundApplyReq.getRenterCashCodeEnum().getTxt());
        String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(cashierRefundApplyEntity));

        cashierRefundApplyEntity.setPayMd5(payMd5);
//        CashierRefundApplyEntity entity = cashierRefundApplyMapper.selectRefundByQn(cashierRefundApplyReq.getMemNo(),cashierRefundApplyReq.getOrderNo(),cashierRefundApplyReq.getQn());
        CashierRefundApplyEntity entity = cashierRefundApplyMapper.selectRefundByMd5(cashierRefundApplyReq.getMemNo(),cashierRefundApplyReq.getOrderNo(),payMd5);
        //判断是否已经存在。
        if(Objects.nonNull(entity) && Objects.nonNull(entity.getId())){
            return entity.getId();
        }
        int result = cashierRefundApplyMapper.insertSelective(cashierRefundApplyEntity);
        if(result==0){
            throw new CashierRefundApplyException();
        }
        return cashierRefundApplyEntity.getId();
    }


    /**
     * 退款回调信息
     */
    public void updateRefundDepositSuccess(AutoPayResultVo notifyDataVo) {
        //1 校验
        String refundIdStr = notifyDataVo.getRefundId();
        int refundId = StringUtil.isBlank(refundIdStr)?0:Integer.valueOf(refundIdStr);
        CashierRefundApplyEntity cashierRefundApplyEntity = cashierRefundApplyMapper.selectByPrimaryKey(refundId);
        //2 回调退款是否成功判断 TODOD
        if(Objects.nonNull(cashierRefundApplyEntity) && CashierRefundApplyStatus.RECEIVED_REFUND.getCode().equals(notifyDataVo.getTransStatus())){
            //3 更新退款成功
            CashierRefundApplyEntity cashierRefundApplyUpdate = new CashierRefundApplyEntity();
            cashierRefundApplyUpdate.setStatus(notifyDataVo.getTransStatus());
            cashierRefundApplyUpdate.setVersion(cashierRefundApplyEntity.getVersion());
            cashierRefundApplyUpdate.setId(cashierRefundApplyEntity.getId());
            cashierRefundApplyUpdate.setRefundTime(LocalDateTime.now());
            int result = cashierRefundApplyMapper.updateByPrimaryKeySelective(cashierRefundApplyUpdate);
            if(result==0){
                throw new OrderPayRefundCallBackAsnyException();
            }
            //4.如果是预授权完成的操作成功，检测该订单是否存在预授权解冻的记录。修改status=01退款中。 todo huangjing
            
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

    /**
     *
     * @param orderNo
     * @return
     */
    public CashierRefundApplyEntity selectorderNo(String orderNo,String payKind) {
        return cashierRefundApplyMapper.selectRefundByType(orderNo,payKind);
    }
    /**
     * 查询订单下所以退款
     * @param orderNo
     * @return
     */
    public List<CashierRefundApplyEntity> getRefundApplyByOrderNo(String orderNo) {
        return cashierRefundApplyMapper.getRefundApplyByOrderNo(orderNo);
    }

    public List<CashierRefundApplyEntity> selectorderNoWaitingAll() {
        //回去
        long refundWatingDaysLong = Long.parseLong(refundWatingDays);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = now.plusDays(-refundWatingDaysLong);
        List<CashierRefundApplyEntity> result = cashierRefundApplyMapper.getCashierRefundApplyByTime(date);
        return result;
    }
}
