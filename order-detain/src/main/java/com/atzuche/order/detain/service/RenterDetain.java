package com.atzuche.order.detain.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.OrderRenterDepositWZDetainReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import com.atzuche.order.commons.enums.detain.DetainStatusEnum;
import com.atzuche.order.detain.entity.RenterDetainUnfreezeEntity;
import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import com.atzuche.order.detain.entity.RenterEventDetainStatusEntity;
import com.atzuche.order.detain.vo.RenterDetainVO;
import com.atzuche.order.detain.vo.UnfreezeRenterDetainVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 暂扣服务
 */
@Service
public class RenterDetain {
    @Autowired private RenterDetainUnfreezeService renterDetainUnfreezeService;
    @Autowired private RenterEventDetainService renterEventDetainService;
    @Autowired private RenterEventDetainStatusService renterEventDetainStatusService;
    @Autowired private CashierService cashierService;
    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired
    AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired OrderService orderService;


    /**
     * 暂扣事件解冻
     * TODO 暂不支付 多部门 冻结解冻
     */
    public void unfreezeRenterDetain(UnfreezeRenterDetainVO unfreezeRenterDetainVO){
        //查询根据订单号 查询订单
        OrderEntity orderEntity = orderService.getOrderEntity(unfreezeRenterDetainVO.getOrderNo());
        Assert.notNull(orderEntity,"订单不存在");
        //1更新暂扣事件
        RenterEventDetainEntity renterEventDetainEntity = renterEventDetainService.getEventDetainByOrderNoAndEvent(unfreezeRenterDetainVO.getOrderNo(),unfreezeRenterDetainVO.getEventType());
        // 2更新暂扣状态表已更新
        renterEventDetainStatusService.updateEventDetainStatus(renterEventDetainEntity);
        int amt =0;
        //3 收银台 暂扣全部金额
        if(DetailSourceEnum.RENT_DEPOSIT.getCode().equals(renterEventDetainEntity.getEventType())){
            DetainRenterDepositReqVO detainRenterDepositReqVO = getUnfreezeDetainRenterDepositReqVO(unfreezeRenterDetainVO);
            amt = -detainRenterDepositReqVO.getAmt();
            detainRenterDepositReqVO.setMemNo(orderEntity.getMemNoRenter());
            cashierService.detainRenterDeposit(detainRenterDepositReqVO);
        }
        if(DetailSourceEnum.WZ_DEPOSIT.getCode().equals(renterEventDetainEntity.getEventType())){
            OrderRenterDepositWZDetainReqVO orderRenterDepositWZDetainReqVO = getUnfreezeDetainRenterWZDepositReqVO(unfreezeRenterDetainVO);
            amt = -orderRenterDepositWZDetainReqVO.getAmt();
            orderRenterDepositWZDetainReqVO.setMemNo(orderEntity.getMemNoRenter());
            cashierService.detainRenterWZDeposit(orderRenterDepositWZDetainReqVO);
        }
        //4 记录解冻记录
        RenterDetainUnfreezeEntity renterDetainUnfreezeEntity = getRenterDetainUnfreezeEntity(unfreezeRenterDetainVO,amt,renterEventDetainEntity.getId());
        renterDetainUnfreezeEntity.setMemNo(orderEntity.getMemNoRenter());
        renterDetainUnfreezeService.insertDetainUnfreeze(renterDetainUnfreezeEntity);
    }

    private RenterDetainUnfreezeEntity getRenterDetainUnfreezeEntity(UnfreezeRenterDetainVO unfreezeRenterDetainVO,int amt,int renterEventDetainId) {
        RenterDetainUnfreezeEntity entity = new RenterDetainUnfreezeEntity();
        BeanUtils.copyProperties(unfreezeRenterDetainVO,entity);
        entity.setUnfreezeAmt(amt);
        entity.setRenterEventDetainId(renterEventDetainId);
        return entity;
    }

    private OrderRenterDepositWZDetainReqVO getUnfreezeDetainRenterWZDepositReqVO(UnfreezeRenterDetainVO unfreezeRenterDetainVO) {
        OrderRenterDepositWZDetainReqVO result = new OrderRenterDepositWZDetainReqVO();
        BeanUtils.copyProperties(unfreezeRenterDetainVO,result);
        result.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT);
        result.setAmt(-cashierService.getRenterDetain(unfreezeRenterDetainVO.getOrderNo(),RenterCashCodeEnum.ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT));
        return result;
    }

    private DetainRenterDepositReqVO getUnfreezeDetainRenterDepositReqVO(UnfreezeRenterDetainVO unfreezeRenterDetainVO) {
        DetainRenterDepositReqVO result = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(unfreezeRenterDetainVO,result);
        result.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_ACCOUNT_DEPOSIT_DETAIN_CAR_AMT);
        result.setAmt(-cashierService.getRenterDetain(unfreezeRenterDetainVO.getOrderNo(),RenterCashCodeEnum.ACCOUNT_DEPOSIT_DETAIN_CAR_AMT));
        return result;
    }

    /**
     * 添加暂扣事件操作
     */
    public void insertRenterDetain(RenterDetainVO renterDetainVO){
        //查询根据订单号 查询订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterDetainVO.getOrderNo());
        Assert.notNull(orderEntity,"订单不存在");
        //1记录暂扣事件
        RenterEventDetainEntity renterEventDetainEntity = getRenterEventDetainEntity(renterDetainVO,orderEntity.getMemNoRenter());
        renterEventDetainService.insertEventDetain(renterEventDetainEntity);
        // 2更新暂扣状态表已更新
        RenterEventDetainStatusEntity renterEventDetainStatusEntity = getRenterEventDetainStatusEntity(renterDetainVO,orderEntity.getMemNoRenter());
        renterEventDetainStatusService.saveEventDetainStatus(renterEventDetainStatusEntity);
        //3 收银台 暂扣全部金额
        if(DetailSourceEnum.RENT_DEPOSIT.equals(renterDetainVO.getEventType())){
            DetainRenterDepositReqVO detainRenterDepositReqVO = getDetainRenterDepositReqVO(renterDetainVO,orderEntity.getMemNoRenter());
            cashierService.detainRenterDeposit(detainRenterDepositReqVO);
        }
        if(DetailSourceEnum.WZ_DEPOSIT.equals(renterDetainVO.getEventType())){
            OrderRenterDepositWZDetainReqVO orderRenterDepositWZDetainReqVO = getDetainRenterWZDepositReqVO(renterDetainVO,orderEntity.getMemNoRenter());
            orderRenterDepositWZDetainReqVO.setMemNo(orderEntity.getMemNoRenter());
            cashierService.detainRenterWZDeposit(orderRenterDepositWZDetainReqVO);
        }
    }


    /**
     *
     * @param renterDetainVO
     * @return
     */
    private OrderRenterDepositWZDetainReqVO getDetainRenterWZDepositReqVO(RenterDetainVO renterDetainVO,String memNo) {
        OrderRenterDepositWZDetainReqVO result = new OrderRenterDepositWZDetainReqVO();
        BeanUtils.copyProperties(renterDetainVO,result);
        result.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT);
        AccountRenterWZDepositResVO vo = accountRenterWzDepositService.getAccountRenterWZDeposit(renterDetainVO.getOrderNo(),memNo);
        result.setAmt(vo.getShouldReturnDeposit());
        return result;
    }

    /**
     * 构造参数
     * @return
     */
    private DetainRenterDepositReqVO getDetainRenterDepositReqVO(RenterDetainVO renterDetainVO,String memNo) {
        DetainRenterDepositReqVO result = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(renterDetainVO,result);
        result.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_DEPOSIT_DETAIN_CAR_AMT);
        AccountRenterDepositResVO vo = accountRenterDepositService.getAccountRenterDepositEntity(renterDetainVO.getOrderNo(),memNo);
        result.setAmt(vo.getSurplusDepositAmt());
        return result;
    }

    /**
     * 构造暂扣状态 参数
     * @param renterDetainVO
     * @return
     */
    private RenterEventDetainStatusEntity getRenterEventDetainStatusEntity(RenterDetainVO renterDetainVO,String memNo) {
        RenterEventDetainStatusEntity entity = new RenterEventDetainStatusEntity();
        entity.setOrderNo(renterDetainVO.getOrderNo());
        entity.setRenterOrderNo(renterDetainVO.getRenterOrderNo());
        entity.setMemNo(memNo);
        entity.setStatus(DetainStatusEnum.DETAIN.getCode());
        entity.setStatusDesc(DetainStatusEnum.DETAIN.getMsg());
        return entity;
    }
    /**
     * 构造暂扣事件 参数
     * @param renterDetainVO
     * @return
     */
    private RenterEventDetainEntity getRenterEventDetainEntity(RenterDetainVO renterDetainVO,String memNo) {
        RenterEventDetainEntity entity = new RenterEventDetainEntity();
        entity.setOrderNo(renterDetainVO.getOrderNo());
        entity.setRenterOrderNo(renterDetainVO.getRenterOrderNo());
        entity.setMemNo(memNo);
        entity.setDeptId(renterDetainVO.getDeptId());
        entity.setDeptName(renterDetainVO.getDeptName());
        entity.setEventType(renterDetainVO.getEventType().getCode());
        entity.setEventName(renterDetainVO.getEventType().getMsg());
        entity.setEventDesc(renterDetainVO.getEventDesc());
        entity.setFreezeStatus(YesNoEnum.YES.getCode());
        return entity;
    }

    /**
     * 查询订单暂扣信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public RenterEventDetainStatusEntity getRenterDetain(String orderNo, String memNo) {
        return renterEventDetainStatusService.getRenterDetainStatus(orderNo, memNo);
    }

    /**
     * 查询订单暂扣信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public Integer getRenterDetainStatus(String orderNo, String memNo) {
        RenterEventDetainStatusEntity renterEventDetainStatus = this.getRenterDetain(orderNo,memNo);
        if(Objects.nonNull(renterEventDetainStatus) && Objects.nonNull(renterEventDetainStatus.getStatus())){
            return renterEventDetainStatus.getStatus();
        }
        return null;
    }
}
