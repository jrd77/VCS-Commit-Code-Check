package com.atzuche.order.coreapi.service.console;

import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.vo.res.WzDepositMsgResVO;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.WzCostLogDTO;
import com.atzuche.order.commons.entity.dto.WzDepositMsgDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.vo.res.console.ConsoleOrderWzDetailQueryResVO;
import com.atzuche.order.detain.dto.CarDepositTemporaryRefundReqDTO;
import com.atzuche.order.detain.service.RenterDetain;
import com.atzuche.order.detain.vo.RenterDetainVO;
import com.atzuche.order.detain.vo.UnfreezeRenterDetainVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.WzCostLogEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.WzCostLogService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Resource
    private CashierQueryService cashierQueryService;
    @Resource
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Resource
    private RenterOrderService renterOrderService;
    @Resource
    private WzCostLogService wzCostLogService;



    /**
     * 违章押金暂扣
     *
     * @param orderNo 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void wzDepositDetainHandle(String orderNo) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);

        if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getWzSettleStatus()
                || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
            log.error("已经结算不允许编辑orderNo:[{}]", orderNo);
            throw new NotAllowedEditException();
        }

        //违章押金暂扣
        RenterDetainVO renterDetainVO = new RenterDetainVO();
        renterDetainVO.setOrderNo(orderNo);
        renterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
        renterDetain.insertRenterDetain(renterDetainVO);

        //订单状态-->违章押金暂扣状态更新
        OrderStatusEntity record = new OrderStatusEntity();
        record.setIsDetainWz(OrderConstant.ONE);
        record.setOrderNo(orderNo);
        orderStatusService.updateRenterOrderByOrderNo(record);
    }

    /**
     * 违章押金暂扣撤销
     *
     * @param orderNo 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void wzDepositUndoDetainHandle(String orderNo) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);

        if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getWzSettleStatus()
                || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
            log.error("已经结算不允许编辑orderNo:[{}]", orderNo);
            throw new NotAllowedEditException();
        }
        //违章押金暂扣撤销
        UnfreezeRenterDetainVO unfreezeRenterDetainVO = new UnfreezeRenterDetainVO();
        unfreezeRenterDetainVO.setOrderNo(orderNo);
        unfreezeRenterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
        renterDetain.unfreezeRenterDetain(unfreezeRenterDetainVO);

        //订单状态-->违章押金暂扣状态更新
        OrderStatusEntity record = new OrderStatusEntity();
        record.setIsDetainWz(OrderConstant.TWO);
        record.setOrderNo(orderNo);
        orderStatusService.updateRenterOrderByOrderNo(record);
    }


    /**
     * 车辆押金暂扣
     *
     * @param carDepositTemporaryRefund 暂扣信息
     */
    public void carDepositDetainHandle(CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(carDepositTemporaryRefund.getOrderNo());

        if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getWzSettleStatus()
                || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
            log.error("已经结算不允许编辑orderNo:[{}]", carDepositTemporaryRefund.getOrderNo());
            throw new NotAllowedEditException();
        }

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
        if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getWzSettleStatus()
                || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
            log.error("已经结算不允许编辑orderNo:[{}]", carDepositTemporaryRefund.getOrderNo());
            throw new NotAllowedEditException();
        }
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


    /**
     * 管理后台违章明细查询需要的订单数据
     *
     * @param orderNo 订单号
     * @return ConsoleOrderWzDetailQueryResVO
     */
    public ConsoleOrderWzDetailQueryResVO getOrderWzDetailByOrderNo(String orderNo) {
        ConsoleOrderWzDetailQueryResVO res = new ConsoleOrderWzDetailQueryResVO();
        //订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if (Objects.nonNull(orderStatusEntity)) {
            OrderStatusDTO dto = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity, dto);
            res.setOrderStatus(dto);
        }

        //订单违章押金信息
        WzDepositMsgResVO vo = cashierQueryService.queryWzDepositMsg(orderNo);
        if (Objects.nonNull(vo)) {
            WzDepositMsgDTO dto = new WzDepositMsgDTO();
            BeanUtils.copyProperties(vo, dto);
            res.setWzDepositMsg(dto);
        }

        //违章费用信息
        List<RenterOrderWzCostDetailEntity> list = renterOrderWzCostDetailService.queryInfosByOrderNo(orderNo);
        if (CollectionUtils.isNotEmpty(list)) {
            List<RenterOrderWzCostDetailDTO> dtos = new ArrayList<>(list.size());
            list.forEach(x -> {
                RenterOrderWzCostDetailDTO dto = new RenterOrderWzCostDetailDTO();
                BeanUtils.copyProperties(x, dto);
                dtos.add(dto);
            });
            res.setDtos(dtos);
        }
        //租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.nonNull(renterOrderEntity)) {
            RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity, renterOrderDTO);
            res.setRenterOrderDTO(renterOrderDTO);
        }
        return res;
    }


    /**
     * 获取订单违章费用变更日志
     *
     * @param orderNo 订单号
     * @return List<WzCostLogDTO>
     */
    public List<WzCostLogDTO> getWzCostOptLogByOrderNo(String orderNo) {
        List<WzCostLogDTO> dtos = new ArrayList<>();
        List<WzCostLogEntity> wzCostLogEntities = wzCostLogService.queryWzCostLogsByOrderNo(orderNo);
        if (CollectionUtils.isNotEmpty(wzCostLogEntities)) {
            wzCostLogEntities.forEach(log -> {
                WzCostLogDTO dto = new WzCostLogDTO();
                BeanUtils.copyProperties(log, dto);
                dtos.add(dto);
            });
        }
        return dtos;
    }


    /**
     * 保存违章费用变更日志
     *
     * @param req 请求参数
     */
    public void saveWzCostOptLog(WzCostLogDTO req) {
        if (Objects.nonNull(req)) {
            WzCostLogEntity wzCostLogEntity = new WzCostLogEntity();
            BeanUtils.copyProperties(req, wzCostLogEntity);
            wzCostLogService.save(wzCostLogEntity);
        }

    }

    /**
     * 维护订单违章费用明细
     *
     * @param req 请求参数
     */
    public void saveWzCostDetail(RenterOrderWzCostDetailDTO req) {
        if(Objects.nonNull(req)) {
            //先将之前的置为无效
            renterOrderWzCostDetailService.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(req.getOrderNo(),
                    req.getCarPlateNum(), req.getMemNo(),
                    OrderConstant.ONE, req.getCostCode());

            RenterOrderWzCostDetailEntity record = new RenterOrderWzCostDetailEntity();
            BeanUtils.copyProperties(req, record);
            renterOrderWzCostDetailService.saveRenterOrderWzCostDetail(record);
        }
    }


}
