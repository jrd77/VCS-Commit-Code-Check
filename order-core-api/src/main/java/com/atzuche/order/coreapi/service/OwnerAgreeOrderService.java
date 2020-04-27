package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atzuche.order.coreapi.filter.StockFilter;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.OwnerAgreeTypeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
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
import com.atzuche.order.search.OrderSearchProxyService;
import com.atzuche.order.search.dto.ConflictOrderSearchReqDTO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPaySourceConstant;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;

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
    private RefuseOrderCheckService refuseOrderCheckService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private StockProxyService stockService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OrderActionMqService orderActionMqService;
    @Autowired
    private OrderStatusMqService orderStatusMqService;
    @Autowired
    private OwnerRefuseOrderService ownerRefuseOrderService;
    @Autowired
    private OrderSearchProxyService orderSearchProxyService;
    @Autowired 
    private PayCallbackService payCallbackService;
    @Autowired
    private CashierPayService cashierPayService;
    @Autowired
    private OrderSourceStatService orderSourceStatService;


    /**
     * 车主同意接单
     *
     * @param reqVO 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void agree(AgreeOrderReqVO reqVO) {
        boolean isConsoleInvoke = null != reqVO.getIsConsoleInvoke() && OrderConstant.YES == reqVO.getIsConsoleInvoke();
        //车主同意前置校验
        refuseOrderCheckService.checkOwnerAgreeOrRefuseOrder(reqVO.getOrderNo(), isConsoleInvoke);

        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
        //扣减库存
        OrderInfoDTO orderInfoDTO = buildReqVO(reqVO.getOrderNo(), ownerOrderEntity);
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
        agreeOrderConflictInfoHandle(reqVO.getOrderNo(), String.valueOf(orderInfoDTO.getCarNo()),
                renterOrderEntity.getExpRentTime(), renterOrderEntity.getExpRevertTime());

        //发送车主同意事件
        orderActionMqService.sendOwnerAgreeOrderSuccess(reqVO.getOrderNo());
        orderStatusMqService.sendOrderStatusByOrderNo(reqVO.getOrderNo(), orderStatusDTO.getStatus(), NewOrderMQStatusEventEnum.ORDER_PREPAY);
        
        //如果是使用钱包，检测是否钱包全额抵扣，推动订单流程。huangjing 200324  刷新钱包
       OrderPaySignReqVO vo = null;
       try {
    	   if(renterOrderEntity.getIsUseWallet() == OrderConstant.YES) {
	    	   vo = cashierPayService.buildOrderPaySignReqVO(renterOrderEntity);
	           cashierPayService.getPaySignStrNew(vo,payCallbackService);
	           logger.info("获取支付签名串B.params=[{}]",GsonUtils.toJson(vo));
    	   }
		} catch (Exception e) {
			logger.error("刷新钱包支付抵扣:params=[{}]",(vo!=null)?GsonUtils.toJson(vo):"EMPTY",e);
		}

    }


	


    /**
     * 租期重叠订单处理
     * <p>租客订单走车主拒单操作</p>
     *
     * @param orderNo    订单号
     * @param carNo      注册号
     * @param rentTime   租期开始时间
     * @param revertTime 租期截止时间
     */
    public int agreeOrderConflictInfoHandle(String orderNo, String carNo, LocalDateTime rentTime,
                                            LocalDateTime revertTime) {
        logger.info("租期重叠订单处理.param is,orderNo:[{}], carNo:[{}], rentTime:[{}], revertTime:[{}]", orderNo, carNo,
                rentTime, revertTime);
        ConflictOrderSearchReqDTO conflictOrderSearchReqDTO = new ConflictOrderSearchReqDTO();
        conflictOrderSearchReqDTO.setOrderNo(orderNo);
        conflictOrderSearchReqDTO.setCarNo(carNo);
        conflictOrderSearchReqDTO.setRentTime(rentTime);
        conflictOrderSearchReqDTO.setRevertTime(revertTime);
        List<com.atzuche.order.search.dto.OrderInfoDTO> orderList = orderSearchProxyService.getAgreeOrderConflictList(conflictOrderSearchReqDTO);
        if (CollectionUtils.isEmpty(orderList)) {
            logger.warn("No overlapping orders found.");
            return 0;
        }
        Map<Integer,List<com.atzuche.order.search.dto.OrderInfoDTO>> map =
                orderList.stream().collect(Collectors.groupingBy(com.atzuche.order.search.dto.OrderInfoDTO::getNewOrOldOrder));
        int success = 0;
        if(!CollectionUtils.isEmpty(map.get(OrderConstant.YES))) {
            for (com.atzuche.order.search.dto.OrderInfoDTO orderInfoDTO : map.get(OrderConstant.YES)) {
                boolean result = false;
                try {
                    ownerRefuseOrderService.refuse(orderInfoDTO.getOrderNo());
                    //后续异步操作可迁移至此
                    result = true;
                } catch (Exception e) {
                    logger.error("租期重叠处理异常.orderInfoDTO:[{}]", JSON.toJSONString(orderInfoDTO), e);
                }
                if (result) {
                    success = success + 1;
                }
            }
        }
        logger.info("Successfully processed [{}] bars", success);

        //发送mq 通知老系统处理重叠订单
        orderActionMqService.sendOrderAgreeConflictNotice(map.get(OrderConstant.NO));
        return success;
    }


    /**
     * 构建扣减库存的请求参数
     *
     * @param orderNo
     * @param ownerOrderEntity
     * @return
     */
    public OrderInfoDTO buildReqVO(String orderNo, OwnerOrderEntity ownerOrderEntity) {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderNo(orderNo);
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if (orderEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }

        if (ownerOrderEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }
        //OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(orderNo, false);
        OrderSourceStatEntity orderSourceStatEntity = orderSourceStatService.selectByOrderNo(orderNo);
        if(orderSourceStatEntity == null){
            logger.error("订单统计信息不存在orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        //logger.info("ownerGoodsDetailDTO is {}", ownerGoodsDetailDTO);
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
        orderInfoDTO.setLongRent(StockFilter.isLongRent(orderSourceStatEntity.getCategory()));
        return orderInfoDTO;
    }
}
