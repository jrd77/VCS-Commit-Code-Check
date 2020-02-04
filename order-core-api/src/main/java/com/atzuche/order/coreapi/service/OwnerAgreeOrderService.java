package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.OwnerAgreeTypeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 车主同意接单
 *
 * @author pengcheng.fu
 * @date 2020/1/9 16:56
 */
@Service
public class OwnerAgreeOrderService {

    private static Logger logger = LoggerFactory.getLogger(OwnerAgreeOrderService.class);

    @Autowired
    RefuseOrderCheckService refuseOrderCheckService;

    @Autowired
    OrderStatusService orderStatusService;

    @Autowired
    RenterOrderService renterOrderService;

    @Autowired
    OrderFlowService orderFlowService;

    @Autowired
    StockProxyService stockService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;




    /**
     * 车主同意接单
     *
     * @param reqVO 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void agree(AgreeOrderReqVO reqVO) {
        //车主同意前置校验
        refuseOrderCheckService.checkOwnerAgreeOrRefuseOrder(reqVO.getOrderNo(), StringUtils.isNotBlank(reqVO.getOperatorName()));

        //扣减库存
        OrderInfoDTO orderInfoDTO = buildReqVO(reqVO.getOrderNo());
        stockService.cutCarStock(orderInfoDTO);


        //变更订单状态
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(reqVO.getOrderNo());
        orderStatusDTO.setStatus(OrderStatusEnum.TO_PAY.getStatus());
        orderStatusDTO.setUpdateOp(reqVO.getOperatorName());
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);

        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(), OrderStatusEnum.TO_PAY);
        //更新租客订单车主同意信息
        RenterOrderEntity renterOrderEntity =
                renterOrderService.getRenterOrderByOrderNoAndIsEffective(reqVO.getOrderNo());

        RenterOrderEntity record = new RenterOrderEntity();
        record.setId(renterOrderEntity.getId());
        record.setReqAcceptTime(LocalDateTime.now());
        record.setAgreeFlag(OwnerAgreeTypeEnum.ARGEE.getCode());
        renterOrderService.updateRenterOrderInfo(record);

        //自动拒绝时间相交的订单
        //TODO:拒绝时间相交的订单

        //消息发送
        //TODO:发送车主同意事件
    }

    /**
     * 构建扣减库存的请求参数
     * @param orderNo
     * @return
     */
    public OrderInfoDTO buildReqVO(String orderNo){
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderNo(orderNo);
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(orderNo,false);
        logger.info("ownerGoodsDetailDTO is {}",ownerGoodsDetailDTO);
        orderInfoDTO.setCarNo(Integer.parseInt(ownerOrderEntity.getGoodsCode()));
        orderInfoDTO.setCityCode(Integer.parseInt(orderEntity.getCityCode()));
        orderInfoDTO.setOldCarNo(null);
        orderInfoDTO.setOperationType(OrderOperationTypeEnum.ZCXD.getType());
        orderInfoDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(ownerOrderEntity.getShowRentTime()));
        orderInfoDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(ownerOrderEntity.getShowRevertTime()));

        orderInfoDTO.setOperationType(OrderOperationTypeEnum.ZCXD.getType());

        LocationDTO getCarAddress = new LocationDTO();
        getCarAddress.setFlag(0);
        LocationDTO returnCarAddress = new LocationDTO();
        returnCarAddress.setFlag(0);

        orderInfoDTO.setGetCarAddress(getCarAddress);
        orderInfoDTO.setReturnCarAddress(returnCarAddress);

        return orderInfoDTO;
    }


}
