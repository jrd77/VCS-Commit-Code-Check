package com.atzuche.order.coreapi.service.console;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import com.atzuche.order.detain.dto.CarDepositTemporaryRefundReqDTO;
import com.atzuche.order.detain.service.RenterDetain;
import com.atzuche.order.detain.vo.RenterDetainVO;
import com.atzuche.order.detain.vo.UnfreezeRenterDetainVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 订单押金管理后台业务处理
 *
 * @author pengcheng.fu
 * @date 2020/4/28 14:19
 */

@Service
@Slf4j
public class ConsoleOrderDepositHandleService {

    @Resource
    private RenterDetain renterDetain;
    @Resource
    private OrderStatusService orderStatusService;


    /**
     * 违章押金暂扣
     *
     * @param orderNo 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void wzDepositDetainHandle(String orderNo) {
        //违章押金暂扣
        RenterDetainVO renterDetainVO = new RenterDetainVO();
        renterDetainVO.setOrderNo(orderNo);
        renterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
        renterDetain.insertRenterDetain(renterDetainVO);

        //订单状态-->违章押金暂扣状态更新
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setIsDetainWz(OrderConstant.ONE);
        orderStatusEntity.setOrderNo(orderNo);
        orderStatusService.updateRenterOrderByOrderNo(orderStatusEntity);
    }

    /**
     * 违章押金暂扣撤销
     *
     * @param orderNo 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void wzDepositUndoDetainHandle(String orderNo) {
        //违章押金暂扣撤销
        UnfreezeRenterDetainVO unfreezeRenterDetainVO = new UnfreezeRenterDetainVO();
        unfreezeRenterDetainVO.setOrderNo(orderNo);
        unfreezeRenterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
        renterDetain.unfreezeRenterDetain(unfreezeRenterDetainVO);

        //订单状态-->违章押金暂扣状态更新
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setIsDetainWz(OrderConstant.TWO);
        orderStatusEntity.setOrderNo(orderNo);
        orderStatusService.updateRenterOrderByOrderNo(orderStatusEntity);
    }


    /**
     * 车辆押金暂扣
     *
     * @param carDepositTemporaryRefund 暂扣信息
     */
    public void carDepositDetainHandle(CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(carDepositTemporaryRefund.getOrderNo());
        if (orderStatusEntity.getIsDetain() != OrderConstant.ONE) {
            //车辆押金暂扣
            RenterDetainVO renterDetainVO = new RenterDetainVO();
            renterDetainVO.setOrderNo(carDepositTemporaryRefund.getOrderNo());
            renterDetainVO.setEventType(DetailSourceEnum.RENT_DEPOSIT);
            renterDetain.insertRenterDetain(renterDetainVO);
            //订单状态-->车辆押金暂扣状态更新
            OrderStatusEntity record = new OrderStatusEntity();
            record.setIsDetain(OrderConstant.ONE);
            record.setOrderNo(carDepositTemporaryRefund.getOrderNo());
            orderStatusService.updateRenterOrderByOrderNo(record);
        }
        //保存车辆押金暂扣信息
        renterDetain.saveRenterDetainReason(carDepositTemporaryRefund);
    }

    /**
     * 车辆押金暂扣撤销
     *
     * @param carDepositTemporaryRefund 暂扣信息
     */
    public void carDepositUndoDetainHandle(CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(carDepositTemporaryRefund.getOrderNo());
        if (orderStatusEntity.getIsDetain() == OrderConstant.ONE) {
            //车辆押金暂扣撤销
            UnfreezeRenterDetainVO unfreezeRenterDetainVO = new UnfreezeRenterDetainVO();
            unfreezeRenterDetainVO.setOrderNo(carDepositTemporaryRefund.getOrderNo());
            unfreezeRenterDetainVO.setEventType(DetailSourceEnum.RENT_DEPOSIT);
            renterDetain.unfreezeRenterDetain(unfreezeRenterDetainVO);
            //订单状态-->车辆押金暂扣状态更新
            OrderStatusEntity record = new OrderStatusEntity();
            record.setIsDetain(OrderConstant.TWO);
            record.setOrderNo(carDepositTemporaryRefund.getOrderNo());
            orderStatusService.updateRenterOrderByOrderNo(record);
        }
        //保存车辆押金暂扣信息
        renterDetain.saveRenterDetainReason(carDepositTemporaryRefund);
    }

}
